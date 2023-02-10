package com.hansung.capstone.board

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FreeBoardFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allPostInterface = GetAllPostInterface.create()

        allPostInterface.getAllPost(0)
            .enqueue(object : Callback<ResultGetAllPost> {
                override fun onResponse(
                    call: Call<ResultGetAllPost>,
                    response: Response<ResultGetAllPost>
                ) {
                    Log.d("결과", "성공 : ${response.body().toString()}")
//                    // 값을 넣어야한다~
                    val body = response.body()
                    activity?.runOnUiThread {
                        val resultAllPost = view.findViewById<RecyclerView>(R.id.resultAllPost)
                        resultAllPost.adapter =
                            body?.let { it -> FreeBoardAdapter(it) }
                    }
                }

                override fun onFailure(call: Call<ResultGetAllPost>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_free_board, container, false)
    }

}