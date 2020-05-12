package com.cyan_namid09.dailygame

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cyan_namid09.dailygame.databinding.ActivityLoginBinding
import com.cyan_namid09.dailygame.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    val provider: OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
    val pendingResultTask: Task<AuthResult>? by lazy {
        auth.pendingAuthResult
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        provider.addCustomParameter("lang", "jp")
        pendingAuth()
        signIn()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        Log.d("user", currentUser?.displayName ?: "")
    }

    private fun pendingAuth() {
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask!!
                .addOnSuccessListener { authResult ->
                    // The OAuth secret can be retrieved by calling:
                    val credential = authResult.credential
                    val profile = authResult.additionalUserInfo?.profile
                    if (credential != null && profile != null) {
                        getSharedPreferences(MAIN_SP, Context.MODE_PRIVATE).apply {
                            edit().putString("token",  (credential as OAuthCredential).accessToken).apply()
                            edit().putString("token_secret",  (credential as OAuthCredential).secret).apply()
                            edit().putString("twitter_id", profile["screen_name"].toString()).apply()
                        }
                    }
                    finish()
                }
                .addOnFailureListener {
                    MaterialAlertDialogBuilder(this).apply {
                        setTitle("認証失敗")
                        setMessage("認証に失敗しました。\n再度、Twitter認証をしてください。")
                        setPositiveButton("OK") { dialog, which ->
                            finish()
                        }
                        setCancelable(false)
                    }.show()
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
        }

    }

    private fun signIn() {
        auth.startActivityForSignInWithProvider( /* activity= */this, provider.build())
            .addOnSuccessListener { authResult ->
                // The OAuth secret can be retrieved by calling:
                val credential = authResult.credential
                val profile = authResult.additionalUserInfo?.profile
                if (credential != null && profile != null) {
                    getSharedPreferences(MAIN_SP, Context.MODE_PRIVATE).apply {
                        edit().putString("token",  (credential as OAuthCredential).accessToken).apply()
                        edit().putString("token_secret",  (credential as OAuthCredential).secret).apply()
                        edit().putString("twitter_id", profile["screen_name"].toString()).apply()
                    }
                }
                finish()
            }
            .addOnFailureListener {
                MaterialAlertDialogBuilder(this).apply {
                    setTitle("認証失敗")
                    setMessage("認証に失敗しました。\n再度、Twitter認証をしてください。")
                    setPositiveButton("OK") { dialog, which ->
                        finish()
                    }
                    setCancelable(false)
                }.show()
            }

    }
}
