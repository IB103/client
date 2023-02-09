package com.hansung.capstone

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.FragmentPostDetailBinding

class PostDetailFragment(var post: Posts) : Fragment() {

//    lateinit var freeBoardActivity : FreeBoardActivity

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
    //    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        freeBoardActivity = context as FreeBoardActivity
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultList = view.findViewById<RecyclerView>(R.id.PostDetailComment)
        activity?.runOnUiThread {
            resultList.adapter =
                PostDetailAdapter(post)
        }

        val title = view.findViewById<EditText>(R.id.PostDetatilTitle)
        val content = view.findViewById<EditText>(R.id.PostDetailDate)
        val date = view.findViewById<EditText>(R.id.PostDetailContent)
        title.setText(post.title)
        date.setText(post.createdDate)
        content.setText(post.content)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

}