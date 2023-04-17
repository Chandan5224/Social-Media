package com.example.socialmedia20.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia20.Adapters.SaveItemClicked
import com.example.socialmedia20.Adapters.SaveRecyclerAdapter
import com.example.socialmedia20.Data.News
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.RoomDatabase.DbAsyncTask
import com.example.socialmedia20.RoomDatabase.NewsDatabase
import com.example.socialmedia20.databinding.FragmentSaveBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SaveFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaveFrag : Fragment(), SaveItemClicked {

    lateinit var binding: FragmentSaveBinding
    var dbNewsList = listOf<com.example.socialmedia20.Data.News>()
    lateinit var mAdapter: SaveRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSaveBinding.inflate(layoutInflater, container, false)

        binding.swipeRefreshLayout.setOnRefreshListener {
            setupRecyclerView()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val state = intArrayOf(1)
        binding.saveRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                state[0] = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && (state[0] == 0 || state[0] == 2)) {
                    binding.linearLayout.visibility = View.GONE
                } else if (dy < -10) {

                    binding.linearLayout.visibility = View.VISIBLE
                }
            }
        })
        setupRecyclerView()


        return binding.root
    }

    private fun setupRecyclerView() {
        dbNewsList = RetrieveSaveData(activity as Context).execute().get()
        if (activity != null) {
            binding.shimmerSave.stopShimmer()
            binding.shimmerSave.visibility = View.GONE
            binding.saveRecyclerView.visibility = View.VISIBLE
            mAdapter = SaveRecyclerAdapter(activity as Context, dbNewsList, this)
            binding.saveRecyclerView.adapter = mAdapter
            binding.saveRecyclerView.layoutManager = LinearLayoutManager(activity as Context)

        }
    }

    class RetrieveSaveData(val context: Context) :
        AsyncTask<Void, Void, List<com.example.socialmedia20.Data.News>>() {
        override fun doInBackground(vararg params: Void?): List<com.example.socialmedia20.Data.News> {
            val db = NewsDatabase.getDatabase(context)
            return db.newsDao().getAll()
        }

    }

    override fun onItemClicked(item: News) {
        // custom browse
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()

        customTabsIntent.launchUrl(binding.root.context, Uri.parse(item.url))

    }

    override fun onShareClick(item: News, imageView: ImageView) {
        val intent = Intent(Intent.ACTION_SEND)

        // for checking the urlToImage is it coming or not?
        if (imageView.height > 10) {
            val mBitmap = imageView.drawable as BitmapDrawable
            val bitmap = mBitmap.bitmap
            val contentResolver = requireActivity().contentResolver
            val pat = MediaStore.Images.Media.insertImage(contentResolver, bitmap, item.title, null)
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pat))
            intent.type = "image/*"
        }
        intent.type = "text/*"
        intent.putExtra(Intent.EXTRA_TEXT, item.title + "\nCheck out here :- " + item.url)
        val chooser = Intent.createChooser(intent, item.title)
        startActivity(chooser, null)
    }

    override fun onDeleteClick(item: News) {
        if (DbAsyncTask(activity as Context, item, 3).execute().get()) {
            setupRecyclerView()
            binding.saveRecyclerView.adapter?.notifyDataSetChanged()
        } else
            Toast.makeText(context, "Some error occurred!!", Toast.LENGTH_SHORT).show()
    }
}