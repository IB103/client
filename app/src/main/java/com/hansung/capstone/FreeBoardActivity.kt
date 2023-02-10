package com.hansung.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FreeBoardActivity : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: FreeBoardActivity? = null
        fun getInstance(): FreeBoardActivity? {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_freeboard)
    }

    fun goPostDetail(post: Posts) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.boardfragmentLayout, PostDetailFragment(post))
            addToBackStack(null)
            commit()
        }
    }
}