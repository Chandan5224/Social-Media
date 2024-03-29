package com.example.socialmedia20.Fragments

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.socialmedia20.R
import com.google.common.reflect.Reflection.getPackageName
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Memes : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var currentImageUrl: String? = null
    lateinit var root: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_memes, container, false) as ViewGroup
        LoadMeme()

        val next = root.findViewById<Button>(R.id.next_Button)
        val share = root.findViewById<Button>(R.id.shareBtn)
        val image = root.findViewById<ImageView>(R.id.memeImage)

        next.setOnClickListener {
            LoadMeme()
        }
        share.setOnClickListener {
            val bar = root.findViewById<LottieAnimationView>(R.id.proBar)
            if (!bar.isVisible) {
                // for image
                val mBitmap = image!!.drawable as BitmapDrawable
                val bitmap = mBitmap.bitmap
                val contentResolver = requireActivity().contentResolver
                val pat = MediaStore.Images.Media.insertImage(contentResolver, bitmap, null, null)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.type = "text/*"
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pat))
                intent.putExtra(Intent.EXTRA_TEXT, "Hey check out this cool meme!!")
                val chooser = Intent.createChooser(intent, "Share this meme using....")
                startActivity(chooser, null)
            }
        }

//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                requireActivity().findViewById<ViewPager>(R.id.viewPager).currentItem = 0
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(callback)

        // Inflate the layout for this fragment
        return root
    }

    private fun shareGif(resourceName: String) {
        val baseDir = Environment.getExternalStorageDirectory().absolutePath
        val fileName = "sharingGif.gif"
        val sharingGifFile = File(baseDir, fileName)
        try {
            val readData = ByteArray(1024 * 500)
            val fis: InputStream = resources.openRawResource(
                resources.getIdentifier(
                    resourceName,
                    "drawable",
                    getPackageName()
                )
            )
            val fos = FileOutputStream(sharingGifFile)
            var i: Int = fis.read(readData)
            while (i != -1) {
                fos.write(readData, 0, i)
                i = fis.read(readData)
            }
            fos.close()
        } catch (io: IOException) {
        }
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/gif"
        val uri = Uri.fromFile(sharingGifFile)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Share Emoji"))
    }

    private fun getPackageName(): String? {
        TODO("Not yet implemented")
    }

    private fun LoadMeme() {
        val queue = Volley.newRequestQueue(context)
        val url = "https://meme-api.com/gimme"
        val Bar = root.findViewById<LottieAnimationView>(R.id.proBar)
        Bar.visibility = View.VISIBLE

// Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                currentImageUrl = response.getString("url")
                val meme = root.findViewById<ImageView>(R.id.memeImage)
                Glide.with(this).load(currentImageUrl).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Bar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Bar.visibility = View.GONE
                        return false
                    }
                }).into(meme)
            },
            { error ->

            }
        )
// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

}