package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.databinding.FragmentMypageBinding
import com.hansung.capstone.retrofit.RetrofitService
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.view_login.view.*
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    var service = retrofit.create(RetrofitService::class.java)
    val REQUEST_CODE = 100
    lateinit var binding: FragmentMypageBinding
    private val DEFAULT_GALLERY_REQUEST_CODE = 0
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
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/"
            startActivityForResult(intent, DEFAULT_GALLERY_REQUEST_CODE)
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
                data ?: return
                //갤러리에서 고른 사진의 uri
                val photouri = data.data as Uri
                val imageview = binding.userContainer.profile_container.profileImage
                imageview.setImageURI(photouri)
            }
            else -> {
                Toast.makeText(activity, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
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
//    fun recreate(){
//        val ft:FragmentTransaction=this.requireFragmentManager().beginTransaction()
//        ft.detach(this)
//        ft.attach(this)
//        ft.commit()
//    }
}