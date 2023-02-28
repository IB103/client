package com.hansung.capstone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hansung.capstone.board.BoardAdapter
import com.hansung.capstone.board.BoardAdapterDecoration
import com.hansung.capstone.board.ResultGetPosts
import com.hansung.capstone.databinding.FragmentBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardFragment : Fragment() {
    lateinit var  binding: FragmentBoardBinding
    private lateinit var resultAllPost: RecyclerView
    private var page = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultAllPost = view.findViewById(R.id.resultAllPost)
        resultAllPost.addItemDecoration(BoardAdapterDecoration())

        val api = CommunityService.create()
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.BoardSwipe)
        swipe.setOnRefreshListener {
            api.getAllPost(0)
                .enqueue(object : Callback<ResultGetPosts> {
                    override fun onResponse(
                        call: Call<ResultGetPosts>,
                        response: Response<ResultGetPosts>
                    ) {
                        Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                        val body = response.body()
                        activity?.runOnUiThread {
                            resultAllPost.adapter =
                                body?.let { it -> context?.let { it1 -> BoardAdapter(it, it1) } }
                        }
                    }

                    override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                        Log.d("getAllPost:", "실패 : $t")
                    }
                })
            swipe.isRefreshing = false

        }

        api.getAllPost(page++)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>
                ) {
                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                    val body = response.body()
                    activity?.runOnUiThread {
                        resultAllPost.adapter =
                            body?.let { it -> context?.let { it1 -> BoardAdapter(it, it1) } }
                    }
                }

                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getAllPost:", "실패 : $t")
                }
            })
        binding.postB.setOnClickListener{
            Log.d("postbt","clicked")
            val intent = Intent(activity, WriteActivity::class.java)
            startActivity(intent)

        }

    }

//    override fun onResume() {
//        super.onResume()
//        val api = CommunityService.create()
//        api.getAllPost(0)
//            .enqueue(object : Callback<ResultGetPosts> {
//                override fun onResponse(
//                    call: Call<ResultGetPosts>,
//                    response: Response<ResultGetPosts>
//                ) {
//                    Log.d("결과", "성공 : ${response.body().toString()}")
//                    val body = response.body()
//                    activity?.runOnUiThread {
//                        resultAllPost.adapter =
//                            body?.let { it -> context?.let { it1 -> BoardAdapter(it, it1) } }
//                    }
//                }
//
//                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
//                    Log.d("결과:", "실패 : $t")
//                }
//            })
//    }
}