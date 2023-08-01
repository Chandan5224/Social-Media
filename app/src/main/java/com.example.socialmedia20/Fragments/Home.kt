@file:Suppress("NAME_SHADOWING")

package com.example.socialmedia20.Fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Adapters.IPostAdapter
import com.example.socialmedia20.Adapters.PostAdapter
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.Data.Report
import com.example.socialmedia20.Data.ReportDao
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.FragmentHomeBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import kotlin.math.log


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

    private lateinit var recycleView: RecyclerView
    lateinit var binding: FragmentHomeBinding
    private lateinit var mAdapter: PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var getImage: ActivityResultLauncher<String?>
    lateinit var dialogPlus: DialogPlus
    var check = true
    lateinit var imageUri: Uri
    private var imageUrl: String = ""


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
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        dialogPlus = DialogPlus.newDialog(context).create()
        setUpRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener {
            val postCollection = postDao.postCollection
            val query = postCollection.orderBy(
                "createdAt",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            val recyclerViewOptions =
                FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
            mAdapter.updateOptions(recyclerViewOptions)

            binding.swipeRefreshLayout.isRefreshing = false
        }

        getImage =
            registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
                if (it != null) {
                    imageUri = it
                    check = false
                    dialogPlus.holderView.findViewById<ImageView>(R.id.editImage).setImageURI(it)
                } else {
                    dialogPlus.holderView.findViewById<ImageView>(R.id.editImage)
                        .setImageDrawable(requireActivity().getDrawable(R.drawable.upload2))
                }
            })

//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (dialogPlus.isShowing) {
//                    dialogPlus.dismiss()
//                } else {
//                    requireActivity().finish()
//                }
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(callback)
        val state = intArrayOf(1)
        binding.trendingView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                state[0] = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && (state[0] == 0 || state[0] == 2)) {
                    requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).visibility =
                        View.GONE
                } else if (dy < -10) {

                    requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).visibility =
                        View.VISIBLE
                }
            }
        })



        return binding.root
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
        recycleView = binding.trendingView
        mAdapter = PostAdapter(recyclerViewOptions, this@Home, activity as Context)
        recycleView.adapter = mAdapter
        recycleView.layoutManager =
            LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false)
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

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onSharePost(post: Post, imageView: ImageView) {
        PostDao().addShare(post.uid)
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
            .setContentHolder(ViewHolder(R.layout.comment_section))
            .setExpanded(true, 1300)
            .setCancelable(true)
            .create()
        val postTitle = dialogPlus.holderView.findViewById<EditText>(R.id.comEditText)
        dialogPlus.show()

        dialogPlus.holderView.findViewById<ImageView>(R.id.comSendBtn).setOnClickListener {
            val comText = postTitle.text.toString().trim()
            if (comText.isNotEmpty()) {
                val postDao = PostDao()
                postDao.addComment(comText, post.uid)
                dialogPlus.dismiss()
            }
        }
        dialogPlus.holderView.findViewById<ImageView>(R.id.cancelBtn).setOnClickListener {
            dialogPlus.dismiss()
        }

    }

    override fun onSave(post: Post) {
        postDao.updateSave(post)
    }


    override fun onMenu(view: View, postId: String, post: Post) {
        val popupMenus = PopupMenu(view.context, view)
        val auth = Firebase.auth
        if (auth.currentUser!!.uid == post.createdBy.uid)
            popupMenus.inflate(R.menu.post_menu)
        else
            popupMenus.inflate(R.menu.post_report_menu)

        popupMenus.setOnMenuItemClickListener {

            val postDao = PostDao()

            when (it.itemId) {
                R.id.reportBtn -> {
                    var reportMes = ""
                    dialogPlus = DialogPlus.newDialog(view.context)
                        .setContentHolder(ViewHolder(R.layout.report_popup))
                        .setExpanded(true, 1300)
                        .setCancelable(true)
                        .create()

                    dialogPlus.show()

                    val view = dialogPlus.holderView
                    view.findViewById<RadioGroup>(R.id.reportRadioGroup)
                        .setOnCheckedChangeListener { group, checkedId ->
                            when (checkedId) {
                                R.id.abuseRBtn -> {
                                    reportMes = "Hateful or abuse content"
                                }
                                R.id.HarmfulRBtn -> {
                                    reportMes = "Harmful or dangerous acts"
                                }
                                R.id.childRBtn -> {
                                    reportMes = "Child abuse"
                                }
                                R.id.sexRBtn -> {
                                    reportMes = "Sex content"
                                }
                                R.id.spamRBtn -> {
                                    reportMes = "Spam or misleading"
                                }
                                R.id.terrorismRBtn -> {
                                    reportMes = "Promote terrorism"
                                }
                                R.id.violentRBtn -> {
                                    reportMes = "Violent or repulsive content"
                                }
                            }
                        }
                    view.findViewById<MaterialButton>(R.id.reportBtn).setOnClickListener {
                        if (reportMes != "") {
                            val user = auth.currentUser!!
                            val report =
                                Report(
                                    getRandomString(15),
                                    user.uid,
                                    user.displayName!!,
                                    user.email!!,
                                    reportMes,
                                    post
                                )
                            ReportDao().addReport(report)
                            Toast.makeText(context, "Report Submitted", Toast.LENGTH_SHORT).show()
                            dialogPlus.dismiss()
                        } else {
                            Toast.makeText(context, "Select one option", Toast.LENGTH_SHORT).show()
                        }
                    }

                    true
                }
                R.id.editBtn -> {

                    dialogPlus = DialogPlus.newDialog(view.context)
                        .setContentHolder(ViewHolder(R.layout.edit_post))
                        .setExpanded(true, 1300)
                        .setCancelable(true)
                        .create()

                    val view = dialogPlus.holderView
                    val image = view.findViewById<ImageView>(R.id.editImage)
                    val text = view.findViewById<EditText>(R.id.editTitle)
                    val saveBtn = view.findViewById<Button>(R.id.saveBtn)
                    val cancelBtn = view.findViewById<ImageView>(R.id.cancelBtn)
                    val title = view.findViewById<TextView>(R.id.title_)

                    title.text = "Edit Post"
                    text.setText(post.text)
                    Glide.with(view.context).load(post.imageUrl).into(image)

                    dialogPlus.show()
                    cancelBtn.setOnClickListener {
                        dialogPlus.dismiss()
                    }
                    dialogPlus.holderView.findViewById<ImageView>(R.id.editImage)
                        .setOnClickListener {
                            requestPermission()
                            getImage.launch("image/*")
                        }

                    saveBtn.setOnClickListener {
                        if (!check) {
                            var storageRef =
                                FirebaseStorage.getInstance().reference.child("images")
                            storageRef = storageRef.child(System.currentTimeMillis().toString())
                            storageRef.putFile(imageUri).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                                        imageUrl = uri.toString()
                                        postDao.updatePost(
                                            text.text.toString(),
                                            imageUrl,
                                            postId
                                        )
                                    }
                                    Toast.makeText(context, "Updated !!", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        it.exception?.message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(context, "Failed !!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            postDao.updatePost(text.text.toString(), post.imageUrl, post.uid)
                        }
                        check = true
                        dialogPlus.dismiss()
                    }
                    true
                }
                R.id.deleteBtn -> {
                    postDao.deletePost(postId)
                    val photoRef: StorageReference =
                        FirebaseStorage.getInstance().getReferenceFromUrl(post.imageUrl)
                    photoRef.delete().addOnSuccessListener {
                        Log.d(TAG, "onSuccess: deleted file");
                    }
                        .addOnFailureListener {
                            Log.d(TAG, "onFailure: did not delete file");
                        }
                    true
                }
                else -> true
            }
        }
        popupMenus.show()
        // Menu buttons icon
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(menu, true)
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun requestPermission() {
        //GuidebyGoogleDevelopers
        if (ContextCompat.checkSelfPermission(
                binding.root.context, android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }

}

