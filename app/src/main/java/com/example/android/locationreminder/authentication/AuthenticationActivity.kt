package com.example.android.locationreminder.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.android.locationreminder.databinding.ActivityAuthenticationBinding
import com.example.android.locationreminder.locationreminders.RemindersActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    companion object {
        const val TAG = "MainFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result ->
            this.onSignInResult(result)
        }

        binding.loginBtn.setOnClickListener {
            launchingSignInFlow()
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,RemindersActivity::class.java)
            startActivity(intent)
        }else{
            Log.i(TAG, "Sign in unsuccessful No user Authenticated")
        }
    }

    private fun launchingSignInFlow(){
        Log.i(TAG, "Started the flow")

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) {
        result?.let {
            val response = result.idpResponse
            if (result.resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Log.i(TAG,"Successfully signed in user ${user?.displayName}!")
                val intent = Intent(this,RemindersActivity::class.java)
                startActivity(intent)
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    override fun onBackPressed() {
        return
    }
}