package com.hansung.capstone

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.board.BoardAdapter
import com.hansung.capstone.board.BoardAdapterDecoration
import com.hansung.capstone.board.ResultGetPosts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardFragment : Fragment() {
    private lateinit var resultAllPost: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("실험","초기화!!!!!!!!!!!!!!!")
        resultAllPost = view.findViewById(R.id.resultAllPost)
        resultAllPost.addItemDecoration(BoardAdapterDecoration())

//        val api = CommunityService.create()
//
//        api.getAllPost(0)
//            .enqueue(object : Callback<ResultGetPosts> {
//                override fun onResponse(
//                    call: Call<ResultGetPosts>,
//                    response: Response<ResultGetPosts>
//                ) {
//                    Log.d("결과", "성공 : ${response.body().toString()}")
//                    val body = response.body()
//                    activity?.runOnUiThread {
//                        val resultAllPost = view.findViewById<RecyclerView>(R.id.resultAllPost)
//                        resultAllPost.adapter =
//                            body?.let { it -> BoardAdapter(it) }
//                        resultAllPost.addItemDecoration(BoardAdapterDecoration())
//                    }
//                }
//
//                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
//                    Log.d("결과:", "실패 : $t")
//                }
//            })
    }

    override fun onResume() {
        super.onResume()

        Log.d("실험","Resume@@@@@@@@@@@@@@@@@@@@2")
        val api = CommunityService.create()

        api.getAllPost(0)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>
                ) {
                    Log.d("결과", "성공 : ${response.body().toString()}")
                    val body = response.body()
                    activity?.runOnUiThread {
//                        val resultAllPost = view?.findViewById<RecyclerView>(R.id.resultAllPost)
                        resultAllPost.adapter =
                            body?.let { it -> BoardAdapter(it) }
//                        resultAllPost.addItemDecoration(BoardAdapterDecoration())
                    }
                }

                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })
    }
}