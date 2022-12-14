package com.example.mds_tp3_quiz

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.mds_tp3_quiz.adapters.ViewPagerAdapter
import com.facebook.FacebookSdk
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private val home = HomeFragment()
    private val ranking = RankingFragment()
    private val profile = ProfileFragment()
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var adapter: ViewPagerAdapter

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragments = arrayListOf(home, ranking, profile)
        adapter = ViewPagerAdapter(fragments, this)

        setupNavigationListener();
        setupViewAdapter();

        FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
        FacebookSdk.sdkInitialize(this)

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build())

        // You must provide a custom layout XML resource and configure at least one
        // provider button ID. It's important that that you set the button ID for every provider
        // that you have enabled.
        // You must provide a custom layout XML resource and configure at least one
        // provider button ID. It's important that that you set the button ID for every provider
        // that you have enabled.

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.auth_method_picker)
            .setGoogleButtonId(R.id.fab_google_login)
            .setEmailButtonId(R.id.fab_email_login)
            .setFacebookButtonId(R.id.fab_facebook_login)
            .build()

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAuthMethodPickerLayout(customLayout)
            .setTheme(R.style.Theme_MDS_TP3_Quiz)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun setupNavigationListener() {
        bottomNavigation = findViewById(R.id.bottom_nav)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mi_home -> {
                    viewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.mi_ranking -> {
                    viewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }
                R.id.mi_profile -> {
                    viewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setupViewAdapter(){
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigation.menu.get(position).setChecked(true)
            }
        })
    }
}