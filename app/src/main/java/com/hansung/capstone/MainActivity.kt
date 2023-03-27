package com.hansung.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hansung.capstone.board.Posts
import com.hansung.capstone.post.PostDetailActivity

class MainActivity : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        var postIdCheck:Long?=null
        var changeAtPostCheck=false
        var deleteCheck=false
        var writeCheck=false
        var modifyCheck=false
        private var instance: MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }
    }
    var modify_title:String=""
    var modify_content:String=""
    var modify_imageList=listOf<Int?>()
    var commentCheck:Int=0

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
    fun deleteCheck(boolean: Boolean){
        deleteCheck=boolean
    }
    fun getdeleteCheck():Boolean{
        return deleteCheck
    }
    fun writeCheck(boolean: Boolean){
        writeCheck=boolean
    }
    fun getwriteCheck():Boolean{
        return writeCheck
    }
    fun setModifyCheck(boolean: Boolean){
        modifyCheck=boolean
    }
    fun getmodifyCheck():Boolean{
        return modifyCheck
        modifyCheck=false
    }
    fun setChangedPostCheck(bool:Boolean){
        changeAtPostCheck=bool
    }
    fun getChangedPostCheck():Boolean{
        return changeAtPostCheck
    }
    fun setModifyInform(title:String, content:String, image: List<Int?>){
        modify_title=title
        modify_content=content
        modify_imageList=image
    }
    fun setPostIdCheck(long:Long){
        postIdCheck=long
    }
    fun getPostIdCheck():Long?{
        return postIdCheck
    }
    fun goPostDetail(post:Posts) {

        val intent = Intent(this, PostDetailActivity::class.java)

        intent.putExtra("id",post.id)
        startActivity(intent)
    }
    //    fun goWriteDetail(post:Posts) {
//        val intent = Intent(this, WriteActivity::class.java)
//        intent.putExtra("id",post.id)
//        startActivity(intent)
//    }
    fun CommentCheck():Int{
        return commentCheck
    }

    fun PostIdCheckSet(long:Long){
        postIdCheck=long
    }
    fun CommentCheckReset(int:Int){
        commentCheck=int
    }
}