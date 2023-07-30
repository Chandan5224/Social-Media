package com.example.socialmedia20.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.socialmedia20.Adapters.VPAdapter
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.Fragments.Home
import com.example.socialmedia20.Fragments.Memes
import com.example.socialmedia20.Fragments.News
import com.example.socialmedia20.Fragments.SaveFrag
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import java.io.File


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var imageUri: Uri
    private var imageUrl: String = ""
    private lateinit var postDao: PostDao
    private lateinit var getImage: ActivityResultLauncher<String?>
    lateinit var dialogPlus: DialogPlus
    var check = true
    var postingCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postDao = PostDao()
        dialogPlus = DialogPlus.newDialog(this).create()
        // set custom toolbar
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        getImage =
            registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
                if (it != null) {
                    imageUri = it
                    dialogPlus.holderView.findViewById<ImageView>(R.id.editImage).setImageURI(it)
                    check = false
                } else {
                    dialogPlus.holderView.findViewById<ImageView>(R.id.editImage)
                        .setImageDrawable(getDrawable(R.drawable.upload2))
                }
            })

        if (!checkForInternet(binding.root.context)) {
            allertShow("Connect to the Internet", "Ok", "", null)
        }
        // Call
        setupViewPager()

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (binding.viewPager.currentItem != 0) {
                    supportActionBar?.hide()
                } else {
                    supportActionBar?.show()
                }
                if (binding.uploadLoader.isShimmerVisible && postingCheck && binding.viewPager.currentItem != 0) {
                    binding.uploadLoader.visibility = View.GONE
                } else if (binding.uploadLoader.isShimmerVisible && postingCheck && binding.viewPager.currentItem == 0) {
                    binding.uploadLoader.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
    }


    private fun setupViewPager() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        val vpAdapter = VPAdapter(supportFragmentManager)
        vpAdapter.apply {
            add(Home(), "")
            add(Memes(), "")
            add(News(), "")
            add(SaveFrag(), "")
        }

        binding.viewPager.adapter = vpAdapter
        binding.tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_home_24).tag = "home"
        binding.tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_tag_faces_24).tag = "meme"
        binding.tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_news).tag = "news"
        binding.tabLayout.getTabAt(3)!!.setIcon(R.drawable.ic_baseline_bookmark_24).tag = "save"
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            resources.getColor(R.color.mainColor),
            PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            resources.getColor(R.color.black),
            PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            resources.getColor(R.color.black),
            PorterDuff.Mode.SRC_IN
        )

        binding.tabLayout.getTabAt(3)!!.icon!!.setColorFilter(
            resources.getColor(R.color.black),
            PorterDuff.Mode.SRC_IN
        )


        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabIconColor = ContextCompat.getColor(this@MainActivity, R.color.mainColor)
                tab!!.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
//                if (tab.tag == "home")
//                    supportActionBar!!.show()
//                else
//                    supportActionBar!!.hide()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabIconColor = ContextCompat.getColor(this@MainActivity, R.color.black)
                tab!!.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }

    /// toolbar set menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu);
        return true
    }

    // menu button click handle
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_account) {
            accountPopup()
        }
        if (item.itemId == R.id.nav_post) {
            createPost()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun accountPopup() {
        dialogPlus = DialogPlus.newDialog(this).setContentHolder(ViewHolder(R.layout.account_popup))
            .setExpanded(true, 1000)
            .setCancelable(true)
            .create()
        dialogPlus.show()
        val view = dialogPlus.holderView
        val userName = view.findViewById<TextView>(R.id.userName)
        val userImage = view.findViewById<ImageView>(R.id.userImage)
        val signOutBtn = view.findViewById<Button>(R.id.signoutBtn)
        val auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        userName.text = auth.currentUser!!.displayName
        Glide.with(userImage.context).load(auth.currentUser!!.photoUrl).circleCrop().into(userImage)
        signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            GoogleSignIn.getClient(binding.root.context, gso).signOut()
            finish()
            val intent = Intent(binding.root.context, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createPost() {
        dialogPlus = DialogPlus.newDialog(this).setContentHolder(ViewHolder(R.layout.edit_post))
            .setExpanded(true, 1300)
            .setCancelable(true)
            .create()
        dialogPlus.show()

        val view = dialogPlus.holderView
        val cancel = view.findViewById<ImageView>(R.id.cancelBtn)
        val postTitle = view.findViewById<EditText>(R.id.editTitle)
        val postBtn = view.findViewById<Button>(R.id.saveBtn)
        val title = view.findViewById<TextView>(R.id.title_)

        title.text = "Create POst"
        postBtn.text = "Post"

        cancel.setOnClickListener {
            dialogPlus.dismiss()
        }
        dialogPlus.holderView.findViewById<ImageView>(R.id.editImage).setOnClickListener {
//            requestPermission()
            getImage.launch("image/*")
        }

        postBtn.setOnClickListener {
            val inputTitle = postTitle.text.toString().trim()
            if (inputTitle.isNotEmpty() && !check) {
                postingCheck = true
                binding.uploadLoader.visibility = View.VISIBLE
                binding.uploadLoader.startShimmer()
                var storageRef = FirebaseStorage.getInstance().reference.child("images")
                storageRef = storageRef.child(System.currentTimeMillis().toString())
                storageRef.putFile(imageUri).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.uploadLoader.stopShimmer()
                        binding.uploadLoader.visibility = View.GONE
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            postDao.addPost(inputTitle, imageUrl)
                        }
                        Toast.makeText(this, "Post Added !!", Toast.LENGTH_SHORT).show()
                        postingCheck = false
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed !!", Toast.LENGTH_SHORT).show()
                }
                check = true
                dialogPlus.dismiss()
            } else {
                Toast.makeText(this, "Please provide title and image !!", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false
            return true
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION") return networkInfo.isConnected
        }
    }

    private fun allertShow(title: String, pos: String, neg: String, dialogPlus: DialogPlus?) {
        val builder = AlertDialog.Builder(binding.root.context)

        builder.apply {
            setTitle(title)
            setPositiveButton(pos) { dialogInterface, which ->
                check = true
                dialogPlus?.dismiss()
            }
            setNegativeButton(neg) { dialogInterface, which ->
                dialogInterface.cancel()
            }
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

//    private fun requestPermission() {
//        //GuidebyGoogleDevelopers
//        if (ContextCompat.checkSelfPermission(
//                binding.root.context, android.Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
//        }
//    }

    override fun onBackPressed() {
        if (dialogPlus.isShowing)
            dialogPlus.dismiss()
        else if (binding.viewPager.currentItem in 1..3)
            binding.viewPager.currentItem = 0
        else if (binding.viewPager.currentItem == 0 && !dialogPlus.isShowing) {
//            this.cacheDir.deleteRecursively() // for delete cache
            super.onBackPressed()
        }
    }


}