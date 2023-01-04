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
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.example.socialmedia20.Activity.MainActivity
import com.example.socialmedia20.Data.MainData
import com.example.socialmedia20.Data.MySingleton
import com.example.socialmedia20.NewsAdapter
import com.example.socialmedia20.NewsItemClicked
import com.example.socialmedia20.databinding.FragmentNewsBinding


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
    private lateinit var recycleView : RecyclerView
    private lateinit var mAdapter: NewsAdapter


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
        binding= FragmentNewsBinding.inflate(layoutInflater)
        recycleView=binding.newsView
        recycleView.layoutManager=LinearLayoutManager(context)
        fetchData()
        mAdapter = NewsAdapter(this)
        recycleView.adapter=mAdapter
        mActivity=(activity as MainActivity)
        val array = arrayOf(1)

        val onScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    // code
                    array[0]=newState
                }
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // code
                    if(dy>0 && (array[0]==0 || array[0]==2))
                    {
                        hideToolbar();
                    }else if(dy<-10){
                        showToolbar();
                    }
                }
            }
        recycleView.addOnScrollListener(onScrollListener)
        return binding.root
    }

    private fun showToolbar() {
        mActivity.binding.toolbarLayout.visibility=View.VISIBLE
    }

    private fun hideToolbar() {
        mActivity.binding.toolbarLayout.visibility=View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment News.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            News().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun fetchData(){
        val url="https://saurav.tech/NewsAPI/top-headlines/category/health/in.json"
        val jsonObjectRequest= JsonObjectRequest(
            com.android.volley.Request.Method.GET,
            url,
            null,
            {
                val newsJsonArray=it.getJSONArray("articles")
                val newsArray=ArrayList<MainData>()
                for(i in 0 until newsJsonArray.length()){
                    val newsJesonObject=newsJsonArray.getJSONObject(i)
                    val news= MainData(
                        newsJesonObject.getString("title"),
                        newsJesonObject.getString("author"),
                        newsJesonObject.getString("url"),
                        newsJesonObject.getString("urlToImage"),
                        newsJesonObject.getString("publishedAt")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            {
                Toast.makeText(context, "Data Not Fetched!!", Toast.LENGTH_SHORT).show()
            }
        )

        MySingleton.getInstance(binding.root.context).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item : MainData)
    {
        // custom browse
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(binding.root.context, Uri.parse(item.url))
    }

    override fun onShareClick(item: MainData, imageView: ImageView) {
        val intent= Intent(Intent.ACTION_SEND)

        // for checking the urlToImage is it coming or not?
        if(imageView.height>10)
        {
            val mBitmap= imageView!!.drawable as BitmapDrawable
            val bitmap=mBitmap.bitmap
            val contentResolver = requireActivity().contentResolver
            val pat= MediaStore.Images.Media.insertImage(contentResolver,bitmap,item.title,null)
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pat))
            intent.type="image/*"
        }
        intent.type="text/*"
        intent.putExtra(Intent.EXTRA_TEXT,item.title+"\nCheck out here :- "+item.url)
        val chooser= Intent.createChooser(intent,item.title)
        startActivity(chooser,null)
    }
}


