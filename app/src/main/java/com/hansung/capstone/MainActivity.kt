package com.hansung.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hansung.capstone.board.Posts
import com.hansung.capstone.post.PostDetailFragment

class MainActivity : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 바텀네비게이션 관련 설정
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentLayout) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.setupWithNavController(navController)
    }

    fun goPostDetail(post: Posts) {
        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.fragmentLayout, PostDetailFragment(post))
            replace(R.id.fragmentLayout, PostDetailFragment(post))
            addToBackStack(null)
            commit()
        }
    }
}