package com.example.kau_oop_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.kau_oop_project.databinding.ActivityPostMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityPostMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tr = supportFragmentManager.beginTransaction()
    }
}
