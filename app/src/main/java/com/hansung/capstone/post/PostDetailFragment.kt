package com.hansung.capstone.post

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.MainActivity
import com.hansung.capstone.board.Posts
import com.hansung.capstone.R

class PostDetailFragment(var post: Posts) : Fragment() {

    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("값 확인", "$post")


        val resultList = view.findViewById<RecyclerView>(R.id.PostDetailComment)
        activity?.runOnUiThread {
            resultList.adapter =
                PostDetailAdapter(post)
        }

        val title = view.findViewById<EditText>(R.id.PostDetailTitle)
        val date = view.findViewById<EditText>(R.id.PostDetailDate)
        val content = view.findViewById<TextView>(R.id.PostDetailContent)
        title.setText(post.title)
//        date.setText(post.createdDate.format(DateTimeFormatter.ofPattern("MM/dd HH:mm")))
        date.setText(post.createdDate)
//        date.setText(freeBoardAdapter?.stringToDate(post.createdDate).toString())
        content.text = post.content
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }
}