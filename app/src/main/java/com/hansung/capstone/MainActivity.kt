package com.hansung.capstone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hansung.capstone.board.Posts
import com.hansung.capstone.post.PostDetailActivity
import com.hansung.capstone.recommend.RecommendFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        var category:String="TOTAL"
        var position:Int=-1
        var postIdCheck:Long=0
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
        bottomNavigationView?.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    //navController.navigate(R.id.homeFragment)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentLayout , HomeFragment())
                        .commit()
                    true
                }
                R.id.myPageFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentLayout, MyPageFragment())
                        .commit()
                    true
                }
                R.id.mapFragment -> {
                   // navController.navigate(R.id.mapFragment)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentLayout,com.hansung.capstone.map.MapFragment())
                        .commit()
                    true
                }
                R.id.boardFragment -> {
                  //  navController.navigate(R.id.boardFragment)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentLayout,BoardFragment())
                        .commit()
                    true
                } R.id.recommendFragment -> {
               //navController.navigate(R.id.recommendFragment)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentLayout,RecommendFragment())
                    .commit()
                true
            }
                else -> false
            }
            //NavigationUI.setupWithNavController(bottomNavigationView, navController)
        }

       // bottomNavigationView?.setupWithNavController(navController)

    }



    fun transfer(){
        bottomNavigationView.menu.findItem(R.id.homeFragment).isChecked = false
        bottomNavigationView.menu.findItem(R.id.myPageFragment).isChecked = true
        //bottomNavigationView.requestFocus(R.id.myPageFragment)
       // navController.navigate(R.id.myPageFragment)
       // findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
    // navController.navigate(R.id.action_homeFragment_to_myPageFragment)

//            Log.d("Checking","${navController.currentDestination?.id }, ${R.id.homeFragment}")
//        if (navController.currentDestination?.id != R.id.homeFragment) {
//            bottomNavigationView.menu.findItem(R.id.homeFragment).isChecked = false
//            bottomNavigationView.menu.findItem(R.id.myPageFragment).isChecked = true
//            navController.navigate(R.id.myPageFragment)
//        } else if (navController.currentDestination?.id == R.id.homeFragment) {
//            navController.popBackStack()
//        }


    }

    fun setCategory(str:String){
        category=str
    }
        fun getCategory():String{
        return category
    }
    fun setPosition(int:Int){
        position=int
    }
        fun getPosition():Int{
        return position
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
    fun getPostIdCheck():Long{
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
    fun goWebPage(uri: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

}




