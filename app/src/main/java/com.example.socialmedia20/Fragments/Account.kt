package com.example.socialmedia20.Fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia20.Adapters.IPostAdapter
import com.example.socialmedia20.Adapters.PostAdapter
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.databinding.FragmentAccountBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.yield

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment(), IPostAdapter {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recycleView: RecyclerView
    lateinit var binding: FragmentAccountBinding
    private lateinit var mAdapter: PostAdapter
    private lateinit var postDao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        setUpRecyclerView()

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Account.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy(
            "createdAt",
            com.google.firebase.firestore.Query.Direction.DESCENDING
        )
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        recycleView = binding.postRecycleView
        mAdapter = PostAdapter(recyclerViewOptions, this@Account)
        recycleView.adapter = mAdapter
        recycleView.layoutManager =
            LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false)
        // recycleView.layoutManager=LinearLayoutManager(context)
//
        binding.scrollView.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            val v=binding.scrollView.getChildAt(binding.scrollView.childCount-1) as View
            val topDetector = binding.scrollView.scrollY
            val bottomDetector =
                v.bottom - (binding.scrollView.height + binding.scrollView.scrollY)
            if (bottomDetector == 0) {
                recycleView.suppressLayout(false)
            }else{
                recycleView.suppressLayout(true)
            }
//
        }
    }

    class LinearLayoutManagerWrapper : LinearLayoutManager {
        constructor(context: Context?) : super(context) {}
        constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
        ) {
        }

        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
        ) : super(context, attrs, defStyleAttr, defStyleRes) {
        }

        override fun supportsPredictiveItemAnimations(): Boolean {
            return false
        }
    }


    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {

    }

    override fun onSharePost(post: Post, imageView: ImageView) {

    }

    override fun onComment(post: Post) {

    }

}