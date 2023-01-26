package com.example.socialmedia20.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia20.Adapters.IPostAdapter
import com.example.socialmedia20.Adapters.PostAdapter
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.FragmentHomeBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */

class Home : Fragment(), IPostAdapter {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recycleView : RecyclerView
    lateinit var binding: FragmentHomeBinding
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
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(layoutInflater)

        setUpRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener {
            val postCollection=postDao.postCollection
            val query=postCollection.orderBy("createdAt",com.google.firebase.firestore.Query.Direction.DESCENDING)
            val recyclerViewOptions=FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
            mAdapter.updateOptions(recyclerViewOptions)

            binding.swipeRefreshLayout.isRefreshing=false
        }
        return binding.root
    }

    private fun setUpRecyclerView() {
        postDao=PostDao()
        val postCollection=postDao.postCollection
        val query=postCollection.orderBy("createdAt",com.google.firebase.firestore.Query.Direction.DESCENDING)
        val recyclerViewOptions=FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
        recycleView=binding.trendingView
        mAdapter = PostAdapter(recyclerViewOptions,this@Home)
        recycleView.adapter=mAdapter
        recycleView.layoutManager=LinearLayoutManagerWrapper(context,LinearLayoutManager.VERTICAL,false)
    // recycleView.layoutManager=LinearLayoutManager(context)

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Trending.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onSharePost(post: Post, imageView: ImageView) {
        val mBitmap = imageView.drawable as BitmapDrawable
        val bitmap = mBitmap.bitmap
        val contentResolver = requireActivity().contentResolver
        val pat = MediaStore.Images.Media.insertImage(contentResolver, bitmap, null, null)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.type = "text/*"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pat))
        intent.putExtra(Intent.EXTRA_TEXT, "${post.text}\nPost By : ${post.createdBy.displayName}")
        val chooser = Intent.createChooser(intent, "Share this meme using....")
        startActivity(chooser, null)

    }

    override fun onComment(post: Post) {
        val dialogPlus = DialogPlus.newDialog(binding.root.context)
            .setContentHolder(ViewHolder(R.layout.edit_post))
            .setExpanded(true,binding.root.height)
            .setCancelable(true)
            .create()
        dialogPlus.holderView.findViewById<TextView>(R.id.title_).text="Comments"
        dialogPlus.show()

    }

}

