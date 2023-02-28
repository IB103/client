package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hansung.capstone.databinding.ActivityWriteBinding
import com.hansung.capstone.retrofit.RepPost
import com.hansung.capstone.retrofit.ReqPost
import com.hansung.capstone.retrofit.RetrofitService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class WriteActivity : AppCompatActivity() {
    val imageList: ArrayList<MultipartBody.Part> = ArrayList()
    var filePart: MultipartBody.Part? = null
    private var photouri: Uri? =null
    private val DEFAULT_GALLERY_REQUEST_CODE = 0
    private var serverinfo = MyApplication.getUrl() //username password1 password2 email
    private var retrofit = Retrofit.Builder().baseUrl("$serverinfo")
        .addConverterFactory(GsonConverterFactory.create()).build()
    private var service = retrofit.create(RetrofitService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.writebutton.isEnabled=false
        binding.editTitle.addTextChangedListener(/* watcher = */ object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.writebutton.isEnabled=false
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                //    Log.d("filesImage","$imageList")
                binding.writebutton.isEnabled =
                    !(binding.editTitle.text.toString()==""||binding.editTitle.text.toString()==null&&binding.editWriting.text.toString()==""||binding.editWriting.text.toString()==null)
            }
        })
        binding.editWriting.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.writebutton.isEnabled=false
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.writebutton.isEnabled =
                    !(binding.editTitle.text.toString()==""||binding.editTitle.text.toString()==null&&binding.editWriting.text.toString()==""||binding.editWriting.text.toString()==null)
            }
        })
        binding.writebutton.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editWriting.text.toString()
            val userId=MyApplication.prefs.getInt("userId",0)
            val postReqPost = ReqPost(userId, title, content)
            Log.d("filesImage","${imageList}")
            service.postCreate(postReqPost, imageList).enqueue(object : Callback<RepPost> {
                //  @SuppressLint("Range")
                override fun onResponse(call: Call<RepPost>, response: Response<RepPost>) {
                    if (response.isSuccessful) {
                        Log.d("req", "OK")
                        val result: RepPost? = response.body()
                        if (response.code() == 201) {//수정해야함
                            if (result?.code == 100) {
                                Log.d("게시글작성", "성공: $title")
                                Log.d("게시글","$result")
                                finish()
                            } else {
                                Log.d("ERR", "실패: " + result?.toString())
                            }
                        }
                    } else {
                        Log.d("ERR", "onResponse 실패")
                    }
                }
                override fun onFailure(call: Call<RepPost>, t: Throwable) {
                    Log.d("onFailure", "실패 ")
                }
            })
        }

        binding.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, DEFAULT_GALLERY_REQUEST_CODE)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        0)
                }
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageView=findViewById<ImageView>(R.id.imageView)
        if (resultCode == Activity.RESULT_OK && requestCode == DEFAULT_GALLERY_REQUEST_CODE) {
            //photouri = data?.data
            val photoUri: Uri? = data?.data
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
                //bitmap = rotateImage(bitmap, 90)
            } catch (e: IOException) {
                e.printStackTrace()
            }
             imageView.setImageBitmap(bitmap)
            Log.d("data.path","${data?.data?.path}")
            Log.d("data","$photouri")
            val filename=getFileName(photoUri!!)
            Log.d("filename","$filename")
            // 선택한 이미지를 imageList에 추가하는 코드
            val inputStream = contentResolver.openInputStream(photoUri)
            val file = File(cacheDir, photoUri.lastPathSegment)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            val requestBody = RequestBody.create(MediaType.parse(contentResolver.getType(photoUri)!!), file)
            filePart = MultipartBody.Part.createFormData("imageList", filename, requestBody)
            imageList.add(filePart!!)
        }
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
    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }
}