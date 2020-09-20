package com.example.demo2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main2.*


class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        button.setOnClickListener {
            when{
                input1.length()<1 -> Toast.makeText(this,"please input name",Toast.LENGTH_SHORT).show()
                input2.length()<1 -> Toast.makeText(this,"please input phone number", Toast.LENGTH_SHORT).show()
                else -> {
                    val b = Bundle()
                    b.putString("name",input1.text.toString())
                    b.putString("phone", input2.text.toString())
                    setResult(Activity.RESULT_OK, Intent().putExtras(b))
                    finish()
                }
            }
        }
    }
}