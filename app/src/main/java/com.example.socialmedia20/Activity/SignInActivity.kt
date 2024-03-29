package com.example.socialmedia20.Activity


import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.socialmedia20.Data.User
import com.example.socialmedia20.Data.UserDao
import com.example.socialmedia20.R
import com.example.socialmedia20.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 123
    private lateinit var binding: ActivitySignInBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(binding.root)
        val logIn = sharedPreferences.getBoolean("agreement", false)

        if (!logIn) {
            showCustomAlertDialog()
        }

        if (!checkForInternet(binding.root.context)) {
            Toast.makeText(this, "Connect To The Internet", Toast.LENGTH_LONG).show()
        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        binding.termBtn.setOnClickListener {
            showCustomAlertDialog()
        }
        binding.signINBtn.setOnClickListener {
            signIn()
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun showCustomAlertDialog() {
        val builder = AlertDialog.Builder(binding.root.context)
        val inflater = LayoutInflater.from(binding.root.context)
        val dialogView = inflater.inflate(R.layout.agreement_popup, null)

        // Customize the content of the alert dialog
        val title = dialogView.findViewById<TextView>(R.id.alertTitle)
        title.text = "Terms & Conditions"

        // Add any other customization for your views

        builder.setView(dialogView)
        builder.setCancelable(false)
        builder.setPositiveButton("Agree") { dialog, _ ->
            // Add any action you want to perform when the positive button is clicked.
            sharedPreferences.edit().putBoolean("agreement", true).apply()
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun signIn() {
        binding.proBarGgg.visibility = View.VISIBLE
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.proBarGgg.visibility = View.GONE
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding.signINBtn.visibility = View.GONE
        binding.proBarGgg.visibility = View.VISIBLE
        binding.welText.visibility = View.GONE
        binding.tagLine.visibility = View.GONE
        binding.lottieAnimationView.visibility = View.GONE
        binding.termView.visibility = View.GONE

        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)
            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            // add user
            val user = User(
                firebaseUser.uid,
                firebaseUser.displayName,
                firebaseUser.photoUrl.toString(),
                firebaseUser.phoneNumber.toString(),
                firebaseUser.email.toString()
            )
            val userDao = UserDao()
            userDao.addUser(user)

            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else {
            binding.signINBtn.visibility = View.VISIBLE
            binding.proBarGgg.visibility = View.GONE
            binding.welText.visibility = View.VISIBLE
            binding.tagLine.visibility = View.VISIBLE
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.termView.visibility = View.VISIBLE
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

}