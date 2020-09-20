package com.example.demo2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_horizon.view.*

data class Item(
    val name: String,
    val phone: String
)
class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private val contact = ArrayList<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearManager = LinearLayoutManager(this)
        linearManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearManager
        adapter = MyAdapter(contact)
        recyclerView.adapter = adapter

        button1.setOnClickListener {
            startActivityForResult(Intent(this, MainActivity2::class.java),1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let {
            if(requestCode==1 && resultCode==Activity.RESULT_OK){
                contact.add(Item(it.getString("name").toString(),it.getString("phone").toString()))
                adapter.notifyDataSetChanged()
            }
        }
    }
}

class MyAdapter(private val mData:ArrayList<Item>): RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val name = v.textView3
        val phone = v.textView4
        val delete = v.imageView2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_horizon, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = mData[position].name
        holder.phone.text = mData[position].phone
        holder.delete.setOnClickListener {
            mData.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}