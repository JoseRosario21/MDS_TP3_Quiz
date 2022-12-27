package com.example.mds_tp3_quiz.presentation.navigation

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.presentation.navigation.adapters.ViewPagerAdapter
import com.example.mds_tp3_quiz.presentation.navigation.fragments.HomeFragment
import com.example.mds_tp3_quiz.presentation.navigation.fragments.ProfileFragment
import com.example.mds_tp3_quiz.presentation.navigation.fragments.RankingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File


class NavigationActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var adapter: ViewPagerAdapter
    private val home = HomeFragment()
    private val ranking = RankingFragment()
    private val profile = ProfileFragment()

    companion object{
        private val REQUEST_CODE:Int = 13
        fun getRequestCode() : Int {
            return REQUEST_CODE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        fragments = arrayListOf(home, ranking, profile)
        adapter = ViewPagerAdapter(fragments, this)

        setupNavigationListener()
        setupViewAdapter()
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
                bottomNavigation.menu[position].isChecked = true
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if (data != null) {
                data.data?.let { profile.setImage(it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}