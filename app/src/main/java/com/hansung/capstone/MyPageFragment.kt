package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hansung.capstone.board.RePModifyProfileImage
import com.hansung.capstone.databinding.FragmentMypageBinding
import com.hansung.capstone.modify.ModifyMyInfo
import com.hansung.capstone.modify.ModifyNickActivity
import com.hansung.capstone.retrofit.ReqModifyProfileImage
import com.hansung.capstone.retrofit.RetrofitService
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.view_login.view.*
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MyPageFragment : Fragment() {

    val api = RetrofitService.create()
    private val requestCode = 100
    lateinit var binding: FragmentMypageBinding
    private val defaultGalleryRequestCode = 0
    private var filePart: MultipartBody.Part? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        if (MyApplication.prefs.getString("email", "") == "") {
            visibleLogin()
        } else {
            visibleProfile()
        }
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun visibleProfile() {
        val noImage:Long=-1
        binding.userContainer.profile_container.visibility = View.VISIBLE
        binding.userContainer.login_container.visibility = View.GONE
        binding.userContainer.profile_container.tv_nick.text =
            MyApplication.prefs.getString("nickname", "")
        binding.userContainer.profile_container.tv_email.text =
            MyApplication.prefs.getString("email", "")
        if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
            binding.userContainer.profile_container.profileImage.setImageResource(R.drawable.user)
        } else {
            getProfileImage()
        }

//        binding.userContainer.profile_container.logout_bt.setOnClickListener {
//
//            if (MyApplication.prefs.getString("state", "") == "kakao") {
//                UserApiClient.instance.logout { error ->
//                    if (error != null) {
//                        Log.e("LOGOUT", "fail", error)
//                    } else {
//                        MyApplication.prefs.setString("id", "")
//                        Log.d("LOGOUT", "success")
//                    }
//                }
//            }
//            MyApplication.prefs.remove()
//            visibleLogin()
//        }

        binding.userContainer.profile_container.profileImage.setOnClickListener {
            val intent = Intent(activity, ModifyMyInfo::class.java)
            startActivity(intent)
        }

        binding.userContainer.profile_container.mypage.setOnClickListener {
            val intent = Intent(activity, ModifyMyInfo::class.java)
            startActivity(intent)
        }
        //내가 쓴 글
        binding.userContainer.profile_container.mystory_bt.setOnClickListener {
            val intent = Intent(activity, MyStory::class.java)
            startActivity(intent)
        }
        //내가 스크랩 글
        binding.userContainer.myscraplist_bt.setOnClickListener {
            val intent = Intent(activity, MyScrap::class.java)
            startActivity(intent)
        }
    }


    private fun visibleLogin() {
        binding.userContainer.login_container.visibility = View.VISIBLE
        binding.userContainer.profile_container.visibility = View.GONE
        binding.userContainer.login_container.login_bt.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, requestCode)
            // startForResult.launch(intent)
        }

    }

    private fun getProfileImage() {
        val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
        Glide.with(requireActivity())
            .load("${MyApplication.getUrl()}profile-image/$profileImageId") // 불러올 이미지 url
            .override(200, 200)
            .centerCrop()
            .into(binding.userContainer.profile_container.profileImage)
    }


    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            if (resultCode == Activity.RESULT_OK ||MyApplication.prefs.getString("email","")!="") {
                Log.d("resultCode","$resultCode")
                Log.d(">>>>>>>>>>>>>>cc", MyApplication.prefs.getString("email", ""))
                Log.d("FINISH",">>>>>>>>>>>>>>")
                visibleProfile()
            } else {
                Log.d("resultCode","$resultCode")
                Log.d("Fail",">>>>>>>>>>>>>>")
                visibleLogin()
            }
        }
        when (requestCode) {
            defaultGalleryRequestCode -> {
                if (resultCode == Activity.RESULT_OK && requestCode == defaultGalleryRequestCode) {
                    val photoUri: Uri = data?.data!!
                    val filename=getFileName(photoUri)
                    Log.d("filename","$filename")
                    // 선택한 이미지 list 추가 코드
                    val inputStream = requireActivity().contentResolver.openInputStream(photoUri)
                    val file = File(requireActivity().cacheDir, photoUri.lastPathSegment!!)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    val requestBody = RequestBody.create(MediaType.parse(requireActivity().contentResolver.getType(photoUri)!!), file)
                    filePart = MultipartBody.Part.createFormData("imageList", filename, requestBody)
                    val image:MultipartBody.Part=filePart!!
                    Log.d("image","$image")
                    modifyImage(filePart!!)
                   // modifyImage(image)
                    //imageList.add(filePart!!)
                }
            }
        }
    }
    fun changed(){
        val noImage:Long=-1
        if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
            binding.userContainer.profile_container.profileImage.setImageResource(R.drawable.user)
        } else {
            Log.d("profileImageId", "${MyApplication.prefs.getLong("profileImageId", 0)}")
            getProfileImage()
        }
        Toast.makeText(context, "프로필 사진이 변경됐습니다.", Toast.LENGTH_SHORT).show()
    }
    private fun modifyImage(image: MultipartBody.Part){
        val userId=MyApplication.prefs.getLong("userId",0)
        val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
        val putModifyProfileImage= ReqModifyProfileImage(userId, profileImageId = profileImageId)
        api.modifyProfileImage(putModifyProfileImage,image).enqueue(object : Callback<RePModifyProfileImage> {
            override fun onResponse(call: Call<RePModifyProfileImage>, response: Response<RePModifyProfileImage>) {
                if (response.isSuccessful) {
                    Log.d( "modifyProfile 성공"," $response")
                    MyApplication.prefs.setLong("profileImageId",  response.body()!!.data.profileImageId)
                    changed()
                   // MainActivity.getInstance()?.setChangedPostCheck(true)
                    }else
                    Log.d("ERR", "onResponse 실패 $response")
                }
            override fun onFailure(call: Call<RePModifyProfileImage>, t: Throwable) {
                Log.d("onFailure", "실패 $t")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(MainActivity.getInstance()?.getLoginState()!!)
            commentLogin()
        if(MyApplication.prefs.getString("email", "")!="")
            visibleProfile()
        else  visibleLogin()
    }
@Suppress("NAME_SHADOWING")
@SuppressLint("Range")
fun getFileName(uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor: Cursor? =requireActivity().contentResolver.query(uri, null, null, null, null)
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
    private fun commentLogin(){
        Toast.makeText(requireActivity(),"로그인 되었습니다.",Toast.LENGTH_SHORT).show()
        MainActivity.getInstance()!!.setLoginState(false)
    }
}