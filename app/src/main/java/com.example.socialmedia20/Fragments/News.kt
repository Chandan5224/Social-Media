package com.example.socialmedia20.Fragments

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.socialmedia20.Activity.MainActivity
import com.example.socialmedia20.Data.News
import com.example.socialmedia20.Data.MySingleton
import com.example.socialmedia20.NewsAdapter
import com.example.socialmedia20.NewsItemClicked
import com.example.socialmedia20.databinding.FragmentNewsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [News.newInstance] factory method to
 * create an instance of this fragment.
 */
class News : Fragment(), NewsItemClicked {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mActivity: MainActivity

    lateinit var binding: FragmentNewsBinding
    private lateinit var recycleView: RecyclerView
    private lateinit var mAdapter: NewsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentNewsBinding.inflate(layoutInflater)
        recycleView = binding.newsView
        recycleView.layoutManager = LinearLayoutManager(context)
        fetchData()
        mAdapter = NewsAdapter(this, requireActivity())
        recycleView.adapter = mAdapter

        mActivity = (activity as MainActivity)

        binding.swipeRefreshLayout.setOnRefreshListener {
            recycleView.adapter?.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    private fun fetchData() {
        val url =
            "https://newsapi.org/v2/top-headlines?country=in&category=business&apiKey=a350e62265ce4d768cebdb7e7abc5e8e"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {

                binding.shimmerNews.stopShimmer()
                binding.shimmerNews.visibility = View.GONE
                binding.newsView.visibility = View.VISIBLE

                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val id= getRandomString(10)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getJSONObject("source").getString("name"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage"),
                        newsJsonObject.getString("publishedAt")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            Response.ErrorListener {
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        MySingleton.getInstance(binding.root.context).addToRequestQueue(jsonObjectRequest)
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
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


}


