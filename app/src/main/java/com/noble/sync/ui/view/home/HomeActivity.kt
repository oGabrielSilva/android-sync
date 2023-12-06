package com.noble.sync.ui.view.home

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.noble.sync.R
import com.noble.sync.adapter.HomeViewPagerAdapter
import com.noble.sync.auth.SyncAuth
import com.noble.sync.databinding.ActivityHomeBinding
import com.noble.sync.util.TabNavigationOption
import com.noble.sync.ui.fragment.ChatFragment
import com.noble.sync.ui.fragment.ExploreFragment
import com.noble.sync.ui.fragment.SettingsFragment
import com.noble.sync.ui.view.auth.SignInActivity
import com.squareup.picasso.Picasso
import java.net.URL

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var authSystem: SyncAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureViewPager2()
        configureAuth()

        Log.d("ERROR3", authSystem.auth.currentUser?.photoUrl.toString())
        reflectAuthOnScreen()
    }

    private fun configureViewPager2() {
        val viewPager2 = binding.viewPager2
        val options = listOf(
            TabNavigationOption(
                getString(R.string.tab_chat_title), ChatFragment()
            ),
            TabNavigationOption(
                getString(R.string.tab_explore_title), ExploreFragment()
            ),
            TabNavigationOption(
                getString(R.string.tab_settings_title), SettingsFragment()
            )
        )
        val adapter = HomeViewPagerAdapter(options, supportFragmentManager, lifecycle)

        viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, viewPager2) { tab, pos ->
            tab.text = options[pos].title
        }.attach()
    }

    private fun configureAuth() {
        authSystem = SyncAuth(Firebase.auth, Firebase.firestore, Firebase.storage)
        if (authSystem.auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun reflectAuthOnScreen() {
        try {
            val photoURL = authSystem.auth.currentUser?.photoUrl
            Log.e("ERROR2", authSystem.auth.currentUser?.photoUrl.toString() ?: "")
            if (photoURL != null) Picasso.get().load(photoURL).into(binding.cardAvatar)
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }
}