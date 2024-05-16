package com.logomann.datascanner20.ui.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.logomann.datascanner20.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}