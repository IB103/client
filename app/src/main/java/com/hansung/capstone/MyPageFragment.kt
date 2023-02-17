package com.hansung.capstone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.hansung.capstone.databinding.FragmentMypageBinding
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.view_login.view.*
import kotlinx.android.synthetic.main.view_profile.view.*

class MyPageFragment : Fragment() {
    val REQUEST_CODE = 100
    lateinit var binding: FragmentMypageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        if (MyApplication.prefs.getString("id", "") == "") {
            visiblelogin()
        } else {
            visibleprofile()
        }
        return binding.root
    }
    private fun visibleprofile() {
        binding.userContainer.profile_container.visibility = View.VISIBLE
        binding.userContainer.login_container.visibility = View.GONE
        binding.userContainer.profile_container.tv_nick.text =
            "${MyApplication.prefs.getString("nickname", "")}"
        binding.userContainer.profile_container.tv_email.text =
            "${MyApplication.prefs.getString("id", "")}"
        binding.userContainer.profile_container.logout_bt.setOnClickListener {
            MyApplication.prefs.remove()
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("LOGOUT", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    //MyApplication.prefs.setString("id", "")
                    Log.d("LOUGOUT", "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
            visiblelogin()
        }



    }

    private fun visiblelogin() {
        binding.userContainer.login_container.visibility = View.VISIBLE
        binding.userContainer.profile_container.visibility = View.GONE
        binding.userContainer.login_container.login_bt.setOnClickListener {
            val intent = Intent(getActivity(), LoginActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
            // startForResult.launch(intent)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK ||MyApplication.prefs.getString("id","")!="") {
                Log.d("resultCode","${resultCode}")
                Log.d(">>>>>>>>>>>>>>cc","${MyApplication.prefs.getString("id", "")}")
                Log.d("FINISH",">>>>>>>>>>>>>>")
                visibleprofile()
            } else {
                Log.d("resultCode","${resultCode}")
                Log.d("Fail",">>>>>>>>>>>>>>")
                visiblelogin()

            }
        }
    }




}