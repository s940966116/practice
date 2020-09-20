package com.example.demo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

data class Item{
    val name: S
}
class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }
}