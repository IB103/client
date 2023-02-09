package com.hansung.capstone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hansung.capstone.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {
    lateinit var  binding : FragmentMypageBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    binding = FragmentMypageBinding.inflate(inflater, container, false)

    binding.loginfor.setOnClickListener{

        val intent = Intent(getActivity(),LoginActivity::class.java)
        startActivity(intent)

    }
    return binding.root
    }
}