package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hansung.capstone.board.ModifyPost
import com.hansung.capstone.databinding.ActivityWriteBinding
import com.hansung.capstone.retrofit.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.fragment_board.*

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


@Suppress("DEPRECATION")
class WriteActivity : AppCompatActivity() {
    private lateinit var rvImage: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var tvImageCount: TextView
    private var countImage = 0
    private var imageUriList = ArrayList<Uri>()
    private var imageIdList = ArrayList<Long?>()
    private var imageId: Long = 0
    private val imageList: ArrayList<MultipartBody.Part> = ArrayList()
    private var filePart: MultipartBody.Part? = null
    private var photoUri: Uri? = null
    private val defaultGalleryRequestCode = 0
    private var service = RetrofitService.create()
    private val binding by lazy { ActivityWriteBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        rvImage = findViewById(R.id.rv_image)

        binding.writebutton.isEnabled = false
        imageAdapter = ImageAdapter(this, binding)
        if (MainActivity.getInstance()?.getModifyCheck()!!) {
            modifyActivity()
            //MainActivity.getInstance()?.setModifyCheck(false)
        }
        initAddImage()
//        binding.freeCategory.setOnClickListener {
//            binding.freeCategory.setTextColor(Color.parseColor("#01DFD7"))
//            binding.courseCategory.setTextColor(Color.parseColor("#A4A4A4"))
//            binding.freeCategory.background=ContextCompat.getDrawable(this,R.drawable.press_border)
//            binding.courseCategory.background=ContextCompat.getDrawable(this,R.drawable.normal_border)
//            category="FREE"
//        }
//        binding.courseCategory.setOnClickListener {
//            binding.courseCategory.setTextColor(Color.parseColor("#01DFD7"))
//            binding.freeCategory.setTextColor(Color.parseColor("#A4A4A4"))
//            binding.courseCategory.background=ContextCompat.getDrawable(this,R.drawable.press_border)
//            binding.freeCategory.background=ContextCompat.getDrawable(this,R.drawable.normal_border)
//            category="COURSE"
//        }
        if (MainActivity.getInstance()?.getModifyCheck() == false) {
            binding.editTitle.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkInputFields()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            binding.editWriting.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkInputFields()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

        }
        binding.writebutton.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editWriting.text.toString()
            val userId = MyApplication.prefs.getLong("userId", 0)
            if (Token().checkToken()) {

                Token().issueNewToken {
                    if (MainActivity.getInstance()?.getModifyCheck()!!)
                        modify(title, userId, content)
                    else createPost(userId, title, content)
                }
            } else {
                if (MainActivity.getInstance()?.getModifyCheck()!!)
                    modify(title, userId, content)
                else createPost(userId, title, content)
            }
        }
    }

    private fun checkInputFields() {
        val input1 = binding.editTitle.text.toString().trim()
        val input2 = binding.editWriting.text.toString().trim()
        binding.writebutton.isEnabled = input1.isNotEmpty() && input2.isNotEmpty()
    }

    private fun createPost(userId: Long, title: String, content: String) {
        binding.progressWrite.visibility = View.VISIBLE
        val postReqPost = ReqPost(userId, title, category = "FREE", content)
        val accessToken = MyApplication.prefs.getString("accessToken", "")
        println("토큰 $accessToken")
        service.postCreate(accessToken = "Bearer $accessToken", requestDTO = postReqPost, imageList)
            .enqueue(object : Callback<RepPost> {
                override fun onResponse(call: Call<RepPost>, response: Response<RepPost>) {
                    if (response.isSuccessful) {
                        Log.d("checkingWriting", "$response")
                        val result: RepPost? = response.body()
                        if (response.code() == 201) {
                            if (result?.code == 100) {
                                Log.d("게시글 작성", "성공: $title")
                                MainActivity.getInstance()?.stateCheck(1)
                                binding.progressWrite.visibility = View.GONE
                                //MainActivity.getInstance()?.writeCheck(true)
                                finish()
                            } else {
                                Log.d("ERR", "실패: $response")
                            }
                        } else {
                            Log.d("ERR", "onResponse 실패")
                            binding.progressWrite.visibility = View.GONE
                            Toast.makeText(this@WriteActivity, "게시글 작성 실패", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Log.d("ERR", "onResponse 실패 $response")
                    }
                }

                override fun onFailure(call: Call<RepPost>, t: Throwable) {
                    Log.d("onFailure", "실패 $t ")
                }
            })
    }

    private fun modify(title: String, user_id: Long, content: String) {
        binding.progressWrite.visibility = View.VISIBLE
        val postId = MainActivity.getInstance()?.getChangedPost()
        val putModifyPost = ReqModifyPost(postId!!.id, title, user_id, content, imageIdList)

        val accessToken = MyApplication.prefs.getString("accessToken", "")
        service.modifyPost(
            accessToken = "Bearer $accessToken",
            requestDTO = putModifyPost,
            imageList
        ).enqueue(object : Callback<ModifyPost> {
            override fun onResponse(
                call: Call<ModifyPost>,
                response: Response<ModifyPost>
            ) {
                if (response.isSuccessful) {
                    Log.d("게시글 수정", "성공: $title")
                    MainActivity.getInstance()?.setModifyCheck(false)
                    MainActivity.getInstance()?.stateCheck(2)
                    binding.progressWrite.visibility = View.GONE
                    Toast.makeText(this@WriteActivity, "게시글이 수정됐습니다.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Log.d("ERR 게시글 수정", "onResponse 실패")
                    binding.progressWrite.visibility = View.GONE
                    Toast.makeText(this@WriteActivity, "게시글 수정 실패", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ModifyPost>, t: Throwable) {
                Log.d("onFailure 게시글 수정", "실패 $t ")
                binding.progressWrite.visibility = View.GONE
                Toast.makeText(this@WriteActivity, "게시글 수정 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun modifyActivity() {
        binding.writebutton.isEnabled = true
        binding.editTitle.setText(MainActivity.getInstance()?.modifyTitle)
        binding.editWriting.setText(MainActivity.getInstance()?.modifyContent)
        countImage = MainActivity.getInstance()?.modifyImagelist!!.size
        if (countImage > 0) {
            addImageId(countImage)
        }

    }

    private fun addImageId(count: Int) {
        for (i in 0 until count) {
            imageId = MainActivity.getInstance()?.modifyImagelist!![i]!!.toLong()
            imageIdList.add(imageId)
            val string =
                "${MyApplication.getUrl()}image/${MainActivity.getInstance()?.modifyImagelist!![i]}"
            val uri: Uri = string.toUri()
            imageUriList.add(uri)
        }
        imageAdapter.addItems(imageUriList)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == defaultGalleryRequestCode) {
            val photoUri: Uri = data?.data!!
            ++countImage
            imageAdapter.addItem(photoUri)
            Log.d("data.path", "${data.data?.path}")
            Log.d("data", "${this.photoUri}")
            val filename = getFileName(photoUri)
            Log.d("filename", "$filename")
            val inputStream = contentResolver.openInputStream(photoUri)
            val file = File(cacheDir, photoUri.lastPathSegment!!)
            val fileSizeInBytes = file.length()
            val fileSizeInMegabytes = fileSizeInBytes / (1024 * 1024)
            if (fileSizeInMegabytes >= 10) {
                resize(photoUri)
            }
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            val requestBody =
                RequestBody.create(
                    MediaType.parse(contentResolver.getType(photoUri)!!),
                    file
                )
            filePart =
                MultipartBody.Part.createFormData("imageList", filename, requestBody)
            imageList.add(filePart!!)
        }
    }

    @SuppressLint("CheckResult")
    private fun resize(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .apply(RequestOptions().override(200, 200)) // 이미지 크기 조정
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null && ev != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAddImage() {
        tvImageCount = findViewById(R.id.tv_image_count)
        val addImageView = findViewById<ConstraintLayout>(R.id.cl_add_image)
        addImageView.setOnClickListener {
            addImage()
        }


        rvImage.adapter = imageAdapter
    }

    fun removeImage(inx: Int, uri: Uri) {
        if (imageUriList.contains(uri)) {
            val num = imageUriList.indexOf(uri)
            imageIdList.removeAt(num)
            imageUriList.removeAt(num)
        } else
            imageList.removeAt(inx - imageIdList.size)
        --countImage
    }

    fun removeImageId(inx: Int) {
        imageIdList.removeAt(inx)
        --countImage
    }

    @SuppressLint("SuspiciousIndentation")
    private fun addImage() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (countImage < 6) {
            Log.d("countImage", "$countImage")
            startActivityForResult(intent, defaultGalleryRequestCode)
        } else alertDialog()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                )
            }
        }
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
            .setMessage("이미지는 최대 6까지 선택할 수 있습니다.")
            .setNegativeButton("닫기", null)
        builder.show()
    }
}