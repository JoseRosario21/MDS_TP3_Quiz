package com.example.mds_tp3_quiz.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

class ViewPagerAdapter(val items: ArrayList<Fragment>,
                       val activity: AppCompatActivity): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return  items.size
    }

    override fun createFragment(position: Int): Fragment {
        return items[position]
    }
}