package com.hansung.capstone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hansung.capstone.Constants.OPEN_BOARD_FRAGMENT
import com.hansung.capstone.board.Posts
import com.hansung.capstone.post.PostDetailActivity
import com.hansung.capstone.recommend.RecommendFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        var showPost:Posts?=null
        var commentCount=0
        var deleteCount=0
        var login:Boolean=false
        var position:Int=-1
        var stateCheck=-1
        var modifyCheck=false
        private var instance: MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }
    }

    var modifyTitle:String=""
    var modifyContent:String=""
    var modifyImagelist=listOf<Int?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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

        // 전달받은 데이터 처리
        val data = intent.getStringExtra("openBoardFragment")

        // 프래그먼트 열기
        if(data.equals("openBoard")) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentLayout, BoardFragment())
                .commit()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let {
            val selectedNavItem = it.getIntExtra(OPEN_BOARD_FRAGMENT, 3)

            // 백스택의 액티비티2와 액티비티1 제거
//            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            supportFragmentManager.popBackStack(null, 1)

            if(selectedNavItem==3) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentLayout, BoardFragment())
                    .commit()
            }
            // 보드프래그먼트 선택
            bottomNavigationView.menu.getItem(selectedNavItem).isChecked = true
        }
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
    fun setCommentCount(int:Int){
        if(int==-1)
            commentCount=0
        else commentCount+=int

    }
    fun getCommentCount():Int{
        return commentCount
    }
    fun setDeletedCommentCount(int:Int){
        if(int==-1)
            deleteCount=0
        else deleteCount+=int
    }
    fun getDeletedCommentCount():Int{
        return deleteCount
    }
    fun setLoginState(bool:Boolean){
        login=bool
    } fun getLoginState():Boolean{
        return login

    }
    fun stateCheck(int:Int){
        stateCheck=int
    }
    fun getStateCheck():Int{
        return stateCheck
    }

    fun setModifyCheck(boolean: Boolean){
        modifyCheck=boolean
    }
    @Suppress("UNREACHABLE_CODE")
    fun getModifyCheck():Boolean{
        return modifyCheck
    }

    fun setModifyInform(title:String, content:String, image: List<Int?>){
        modifyTitle=title
        modifyContent=content
        modifyImagelist=image
    }

    fun getChangedPost():Posts{
        return showPost!!
    }
    fun goPostDetail(post:Posts) {

        val intent = Intent(this, PostDetailActivity::class.java)
        showPost=post
        intent.putExtra("postid",post.id)
        startActivity(intent)
    }
    //    fun goWriteDetail(post:Posts) {
//        val intent = Intent(this, WriteActivity::class.java)
//        intent.putExtra("id",post.id)
//        startActivity(intent)
//    }

    fun goWebPage(uri: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

}




