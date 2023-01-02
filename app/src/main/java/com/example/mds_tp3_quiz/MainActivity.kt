package com.example.mds_tp3_quiz

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mds_tp3_quiz.presentation.navigation.NavigationActivity
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val googleLoginResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result != null) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information

                                        db.collection("Users").document(auth.uid!!).get()
                                            .addOnSuccessListener { document ->
                                                if (document.exists()) {
                                                    logInSuccessfully()
                                                } else {
                                                    val newUser = hashMapOf(
                                                        "id" to auth.currentUser?.uid,
                                                        "username" to auth.currentUser?.displayName,
                                                        "email" to auth.currentUser?.email,
                                                        "globalPoints" to 0,
                                                        "totalMatches" to 0,
                                                        "level" to 1,
                                                        "currentExp" to 0
                                                    )

                                                    db.collection("Users").document(auth.uid!!)
                                                        .set(newUser)
                                                        .addOnSuccessListener { logInSuccessfully() }
                                                        .addOnFailureListener { e -> Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show() }
                                                }
                                            }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                } catch (e: ApiException) {
                    println(e.message)
                }
            }
        }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var callbackManager: CallbackManager

    private var email = ""
    private var username = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        oneTapClient = Identity.getSignInClient(this)
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser != null) {
            auth.signOut()
        }
        addListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun addListeners() {
        btn_signup.setOnClickListener { createRegularAccount() }
        btn_signin.setOnClickListener { loginWithEmail() }
        fab_google_login.setOnClickListener { logInViaGoogleAccount() }
        fab_facebook_login.setOnClickListener { logInViaFacebookAccount() }

        tv_login_email_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                email = s.toString()
                checkIfCanLogin()
            }
        })

        tv_login_username_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                username = s.toString()
                checkIfCanLogin()
            }
        })

        tv_login_password_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                password = s.toString()
                checkIfCanLogin()
            }
        })
    }

    private fun checkIfCanLogin() {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            btn_signin.isEnabled = true
            btn_signup.isEnabled = username.isNotEmpty()
        } else {
            btn_signup.isEnabled = false
        }
    }

    private fun logInViaFacebookAccount() {
        val callback : FacebookCallback<LoginResult> = object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Login Canceled", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }
        }

        LoginManager.getInstance().registerCallback(callbackManager, callback)

        fab_facebook_login.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, arrayListOf("email", "public_profile"))
        }
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    db.collection("Users").document(auth.uid!!).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                logInSuccessfully()
                            } else {
                                val newUser = hashMapOf(
                                    "id" to auth.currentUser?.uid,
                                    "username" to auth.currentUser?.displayName,
                                    "email" to auth.currentUser?.email,
                                    "globalPoints" to 0,
                                    "totalMatches" to 0,
                                    "level" to 1,
                                    "currentExp" to 0
                                )

                                db.collection("Users").document(auth.uid!!)
                                    .set(newUser)
                                    .addOnSuccessListener { logInSuccessfully() }
                                    .addOnFailureListener { e -> Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show() }
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun logInViaGoogleAccount() {
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build())
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    googleLoginResultLauncher.launch(intentSenderRequest)
                } catch (e: SendIntentException) {
                    println(e.localizedMessage)
                }
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginWithEmail() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    logInSuccessfully()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createRegularAccount() {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)

                    db.collection("Users").document(auth.uid!!).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                logInSuccessfully()
                            } else {
                                val newUser = hashMapOf(
                                    "id" to auth.currentUser?.uid,
                                    "username" to username,
                                    "email" to email,
                                    "globalPoints" to 0,
                                    "totalMatches" to 0,
                                    "level" to 1,
                                    "currentExp" to 0
                                )
                                db.collection("Users").document(auth.uid!!)
                                    .set(newUser)
                                    .addOnSuccessListener { logInSuccessfully() }
                                    .addOnFailureListener { e -> Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show() }
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Account creation failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun logInSuccessfully() {
        startActivity(Intent(this, NavigationActivity::class.java))
    }
}