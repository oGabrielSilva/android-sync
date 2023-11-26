package com.noble.sync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.noble.sync.view.auth.SignInActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread.sleep(1500)
        startActivity(Intent(applicationContext, SignInActivity::class.java))
        finish()
    }
}