package com.noble.sync.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.noble.sync.util.TabNavigationOption


class HomeViewPagerAdapter(
    private val options: List<TabNavigationOption>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return options.size
    }

    override fun createFragment(position: Int): Fragment {
        return options[position].fragment
    }
}