package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hansung.capstone.*
import com.hansung.capstone.board.RePModifyProfileImage
import com.hansung.capstone.databinding.ActivityModifymyinfoBinding
import com.hansung.capstone.find.FindPwActivity
import com.hansung.capstone.retrofit.*
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ModifyMyInfo:AppCompatActivity() {
    companion object {
        private const val MODIFY_REQUEST_CODE = 123
        private const val MODIFYPWACT_REQUEST_CODE = 12
        private val defaultGalleryRequestCode = 0
    }
    private val binding by lazy { ActivityModifymyinfoBinding.inflate(layoutInflater) }
    var api= RetrofitService.create()
    private var filePart: MultipartBody.Part? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toobar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        getProfileImage()
        info()
        binding.modifyPwActivity.setOnClickListener {
            val intent= Intent(this, FindPwActivity::class.java)
            startActivityForResult(intent, MODIFYPWACT_REQUEST_CODE)
        }
        binding. logoutBt.setOnClickListener {   logOut()
            MyApplication.prefs.remove()
         }
        binding.modifyNickActivity.setOnClickListener {
            val intent= Intent(this,ModifyNickActivity::class.java)
            startActivityForResult(intent, MODIFY_REQUEST_CODE)
        }
        binding.editProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, defaultGalleryRequestCode)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
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
    }
    private fun requestLogOut(){
        val accessToken= MyApplication.prefs.getString("accessToken","")
        api.logOut(accessToken = "Bearer $accessToken").enqueue(object:Callback<RepLogOut>{
            override fun onResponse(call: Call<RepLogOut>, response: Response<RepLogOut>) {
                if(response.code()==200){
                    val result: RepLogOut =response.body()!!
                    MainActivity.getInstance()!!.setLoginState(0)
                    Log.d("resultlogOut","$result")
                    finish()
                    // setData(result.data)
                }
            }
            override fun onFailure(call: Call<RepLogOut>, t: Throwable) {
                Log.d("fail","${t.message}")

            }
        })
    }
    private  fun logOut(){
        if (Token().checkToken()) {
            Token().issueNewToken {
               requestLogOut()
            }
        }else requestLogOut()
    }
    private fun info(){

        binding.tvEmail.text=MyApplication.prefs.getString("email","")
        binding.tvUsername.text=MyApplication.prefs.getString("username","")
        binding.tvNick.text=MyApplication.prefs.getString("nickname","")

    }
    private fun changed(){
        val noImage:Long=-1
        if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
            binding.profileImage.setImageResource(R.drawable.user)
        } else {
            getProfileImage()
        }
        Toast.makeText(this, "프로필 사진이 변경됐습니다.", Toast.LENGTH_SHORT).show()
    }
    private fun getProfileImage() {
        val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
        val noImage:Long=-1

         if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
            binding.profileImage.setImageResource(R.drawable.user)
        }else{
        Glide.with(this)
            .load("${MyApplication.getUrl()}profile-image/$profileImageId") // 불러올 이미지 url
            .override(200, 200)
            .centerCrop()
            .into(binding.profileImage)
    }}
    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =this.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        .also { result = it }
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
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
    private fun modifyImage(image: MultipartBody.Part){
        val userId=MyApplication.prefs.getLong("userId",0)
        val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
        val putModifyProfileImage= ReqModifyProfileImage(userId, profileImageId = profileImageId)
        val accessToken= MyApplication.prefs.getString("accessToken", "")
        api.modifyProfileImage(accessToken = "Bearer $accessToken",putModifyProfileImage,image).enqueue(object :
            Callback<RePModifyProfileImage> {
            override fun onResponse(call: Call<RePModifyProfileImage>, response: Response<RePModifyProfileImage>) {
                if (response.isSuccessful) {
                    MyApplication.prefs.setLong("profileImageId",  response.body()!!.data.profileImageId)
                    changed()
                }else
                    Log.d("ERR", "onResponse 실패 $response")
            }
            override fun onFailure(call: Call<RePModifyProfileImage>, t: Throwable) {
                Log.d("onFailure", "실패 $t")
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MODIFY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            info()
            Toast.makeText(this,"닉네임 변경이 완료됐습니다.", Toast.LENGTH_SHORT).show()
            // completeSignUp 값 활용
        }else if (requestCode == MODIFYPWACT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this,"비밀번호 변경이 완료됐습니다.", Toast.LENGTH_SHORT).show()
        }
        when (requestCode) {
            defaultGalleryRequestCode -> {
                if (resultCode == Activity.RESULT_OK && requestCode == defaultGalleryRequestCode) {
                    val photoUri: Uri = data?.data!!
                    val filename=getFileName(photoUri)
                    Log.d("filename","$filename")
                    // 선택한 이미지 list 추가 코드
                    val inputStream = this.contentResolver.openInputStream(photoUri)
                    val file = File(this.cacheDir, photoUri.lastPathSegment!!)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    val requestBody = RequestBody.create(MediaType.parse(this.contentResolver.getType(photoUri)!!), file)
                    filePart = MultipartBody.Part.createFormData("imageList", filename, requestBody)
                    if (Token().checkToken()) {
                        Token().issueNewToken{modifyImage(filePart!!)}
                    }else{
                        modifyImage(filePart!!)
                    }

                }
            }
        }

    }
}