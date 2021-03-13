package com.siscofran.loplop.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
    }
}