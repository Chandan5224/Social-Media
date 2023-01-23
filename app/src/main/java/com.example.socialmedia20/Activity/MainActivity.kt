package com.example.socialmedia20.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.socialmedia20.Adapters.VPAdapter
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.Fragments.Home
import com.example.socialmedia20.Fragments.Memes
import com.example.socialmedia20.Fragments.News
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


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var imageUri: Uri
    private var imageUrl: String = ""
    private lateinit var postDao: PostDao
    private lateinit var getImage: ActivityResultLauncher<String?>
    private lateinit var dialogView: View
    var check = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postDao = PostDao()
        // set custom toolbar
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)

        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(), ActivityResultCallback {
                dialogView.findViewById<ImageView>(R.id.editImage).setImageURI(it)
                if (it != null) {
                    imageUri = it
                    check = false
                }
            }
        )

        if (!checkForInternet(binding.root.context)) {
            allertShow()
        }
        // Call
        setupViewPager()
    }


    private fun setupViewPager() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        val vpAdapter = VPAdapter(supportFragmentManager)
        vpAdapter.apply {
            add(Home(), "")
            add(Memes(), "")
            add(News(), "")
        }
        binding.viewPager.adapter = vpAdapter
        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_baseline_home_24)
        binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_memes)
        binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_news)
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

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
        val dialog = DialogPlus.newDialog(this)
            .setContentHolder(ViewHolder(R.layout.account_popup))
            .setExpanded(true, 1000)
            .setCancelable(true)
            .create()
        val view = dialog.holderView
        val userName=view.findViewById<TextView>(R.id.userName)
        val userImage=view.findViewById<ImageView>(R.id.userImage)
        val signOutBtn=view.findViewById<Button>(R.id.signoutBtn)
        val auth= Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        userName.text=auth.currentUser!!.displayName
        Glide.with(userImage.context).load(auth.currentUser!!.photoUrl).circleCrop().into(userImage)

        dialog.show()
        signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            GoogleSignIn.getClient(binding.root.context, gso).signOut()
            finish()
            val intent= Intent(binding.root.context, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createPost() {
        val dialogPlus = DialogPlus.newDialog(this)
            .setContentHolder(ViewHolder(R.layout.dialog_plus))
            .setExpanded(true, 1300)
            .setCancelable(true)
            .create()
        dialogPlus.show()

        val view = dialogPlus.holderView
        dialogView = view
        val image = view.findViewById<ImageView>(R.id.editImage)
        val text = view.findViewById<EditText>(R.id.editTitle)
        val postBtn = view.findViewById<Button>(R.id.saveBtn)
        val title = view.findViewById<TextView>(R.id.title_)
        val chooseImage = view.findViewById<Button>(R.id.uploadBtn)

        title.text = "Create POst"
        postBtn.text = "Post"

        chooseImage.setOnClickListener {
            requestPermission()
            getImage.launch("image/*")
        }

        postBtn.setOnClickListener {
            val input = text.text.toString().trim()
            if (input.isNotEmpty() && !check) {
                var storageRef = FirebaseStorage.getInstance().reference.child("images")
                storageRef = storageRef.child(System.currentTimeMillis().toString())
                storageRef.putFile(imageUri).addOnCompleteListener {
                    if (it.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            postDao.addPost(input, imageUrl)
                        }
                        Toast.makeText(this, "Post Added !!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed !!", Toast.LENGTH_SHORT).show()
                    }
                dialogPlus.dismiss()
            } else {
                Toast.makeText(this, "Please provide title and image !!", Toast.LENGTH_LONG).show()
            }
            text.onEditorAction(EditorInfo.IME_ACTION_DONE)
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
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun allertShow() {
        val builder = AlertDialog.Builder(binding.root.context)

        builder.apply {
            setTitle("Connect To The Internet !")
            setIcon(R.drawable.ic_baseline_signal)
            setPositiveButton("OK") { dialogInterface, which ->
            }
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun requestPermission() {
        //GuidebyGoogleDevelopers
        if (ContextCompat.checkSelfPermission(
                binding.root.context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }


}