package com.hansung.capstone

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.hansung.capstone.databinding.FragmentPostDetailBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostDetailFragment(var post: Posts) : Fragment() {

    lateinit var freeBoardActivity: FreeBoardActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        freeBoardActivity = context as FreeBoardActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("값 확인", "$post")


        val resultList = view.findViewById<RecyclerView>(R.id.PostDetailComment)
        activity?.runOnUiThread {
            resultList.adapter =
                PostDetailAdapter(post)
        }

        val title = view.findViewById<EditText>(R.id.PostDetatilTitle)
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

//    private fun stringToDate(date: String): LocalDateTime {
//        val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
//        return LocalDateTime.parse(date, formatter)
//    }

}