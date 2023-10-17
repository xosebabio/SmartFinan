package com.xbs.smartfinan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xbs.smartfinan.R
import com.xbs.smartfinan.databinding.ActivityFormProfileBinding

class FormProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityFormProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}