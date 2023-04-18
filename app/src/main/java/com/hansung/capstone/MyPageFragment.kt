package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.board.RePModifyProfileImage
import com.hansung.capstone.board.ResultGetPosts
import com.hansung.capstone.databinding.FragmentMypageBinding
import com.hansung.capstone.retrofit.RepPost
import com.hansung.capstone.retrofit.ReqModifyPost
import com.hansung.capstone.retrofit.ReqModifyProfileImage
import com.hansung.capstone.retrofit.RetrofitService
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.view_login.view.*
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class MyPageFragment : Fragment() {
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    var server_info = MyApplication.getUrl()//username password1 password2 email
    var clientBuilder = OkHttpClient.Builder()
    var retrofit = Retrofit.Builder().baseUrl("$server_info")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(clientBuilder.build())
        .build()
    val api = RetrofitService.create()
    var service = retrofit.create(RetrofitService::class.java)
    val REQUEST_CODE = 100
    lateinit var binding: FragmentMypageBinding
    private val DEFAULT_GALLERY_REQUEST_CODE = 0
    var filePart: MultipartBody.Part? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        if (MyApplication.prefs.getString("email", "") == "") {
            visiblelogin()
        } else {
            visibleprofile()
        }
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun visibleprofile() {
        Log.d("visibleprofile", "OK")
        binding.userContainer.profile_container.visibility = View.VISIBLE
        binding.userContainer.login_container.visibility = View.GONE
        Log.d("변경된 닉네임", "${MyApplication.prefs.getString("nickname", "")}")
        binding.userContainer.profile_container.tv_nick.text =
            "${MyApplication.prefs.getString("nickname", "")}"
        binding.userContainer.profile_container.tv_email.text =
            "${MyApplication.prefs.getString("email", "")}"
        if (MyApplication.prefs.getInt("profileImageId", 0) == -1) {
            binding.userContainer.profile_container.profileImage.setImageResource(R.drawable.user)
        } else {
            Log.d("profileImageId", "${MyApplication.prefs.getInt("profileImageId", 0)}")
            getprofileImage()
        }
        //이미지 사용자가 변경
        binding.userContainer.profile_container.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, DEFAULT_GALLERY_REQUEST_CODE)
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                )
            }
        }
        }
        //로그아웃
        binding.userContainer.profile_container.logout_bt.setOnClickListener {

            if (MyApplication.prefs.getString("state", "") == "kakao") {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e("LOGOUT", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    } else {
                        MyApplication.prefs.setString("id", "")
                        Log.d("LOUGOUT", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    }
                }
            }
            MyApplication.prefs.remove()
            visiblelogin()
        }
        //닉네임, 비밀 번호 수정하기
        binding.userContainer.profile_container.modify_bt.setOnClickListener {
            val intent = Intent(activity, ModifyNickActivity::class.java)
            startActivity(intent)
        }
        //내가 쓴 글
        binding.userContainer.profile_container.mystory_bt.setOnClickListener {
            val intent = Intent(activity, MyStory::class.java)
            startActivity(intent)
        }
    }


    private fun visiblelogin() {
        binding.userContainer.login_container.visibility = View.VISIBLE
        binding.userContainer.profile_container.visibility = View.GONE
        binding.userContainer.login_container.login_bt.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
            // startForResult.launch(intent)
        }

    }

    private fun getprofileImage() {
        var profileImageId = MyApplication.prefs.getInt("profileImageId", 0)

        service.getProfileImage(profileImageId).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>,
            ) {
                Log.d("결과", "성공 : ${response.body().toString()}")
                val imageB = response.body()?.byteStream()
                val bitmap = BitmapFactory.decodeStream(imageB)
                binding.userContainer.profile_container.profileImage.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK ||MyApplication.prefs.getString("email","")!="") {
                Log.d("resultCode","${resultCode}")
                Log.d(">>>>>>>>>>>>>>cc","${MyApplication.prefs.getString("email", "")}")
                Log.d("FINISH",">>>>>>>>>>>>>>")
                visibleprofile()
            } else {
                Log.d("resultCode","${resultCode}")
                Log.d("Fail",">>>>>>>>>>>>>>")
                visiblelogin()
            }
        }
        when (requestCode) {
            DEFAULT_GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && requestCode == DEFAULT_GALLERY_REQUEST_CODE) {
                    val photoUri: Uri = data?.data!!
                    val filename=getFileName(photoUri)
                    Log.d("filename","$filename")
                    // 선택한 이미지를 imageList에 추가하는 코드
                    val inputStream = requireActivity().contentResolver.openInputStream(photoUri)
                    val file = File(requireActivity().cacheDir, photoUri.lastPathSegment)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    val requestBody = RequestBody.create(MediaType.parse(requireActivity().contentResolver.getType(photoUri)), file)
                    filePart = MultipartBody.Part.createFormData("imageList", filename, requestBody)
                    val image:MultipartBody.Part=filePart!!
                    Log.d("image","${image}")
                    modifyImage(image)
                    //imageList.add(filePart!!)
                }
            }
        }
    }
    fun modifyImage(image:MultipartBody.Part){
        val userId=MyApplication.prefs.getInt("userId",0)
        var profileImageId = MyApplication.prefs.getInt("profileImageId", 0)
        val putModifyProfileImage= ReqModifyProfileImage(userId.toLong(), profileImageId = profileImageId.toLong())

        api.modifyProfileImage(putModifyProfileImage,image).enqueue(object : Callback<RePModifyProfileImage> {
            override fun onResponse(call: Call<RePModifyProfileImage>, response: Response<RePModifyProfileImage>) {
                if (response.isSuccessful) {
                    Log.d("이미지 변경", "성공")
                    Toast.makeText(activity, "프로필 사진이 변경됐습니다.", Toast.LENGTH_SHORT).show()
                    }else
                    Log.d("ERR", "onResponse 실패")
                }
            override fun onFailure(call: Call<RePModifyProfileImage>, t: Throwable) {
                Log.d("onFailure", "실패 ")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        Log.d("onpause",">>>")

    }
    override fun onResume() {
        super.onResume()
        Log.d("onresume","${MyApplication.prefs.getString("nickname", "")}")
        if(MyApplication.prefs.getString("email", "")!="")
            visibleprofile()
    }
@SuppressLint("Range")
fun getFileName(uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor: Cursor? =requireActivity().contentResolver.query(uri, null, null, null, null)
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