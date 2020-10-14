package com.example.demo

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.av.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.http2.Http2Reader
import org.json.JSONObject
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

data class Item(
    val title: String,
    val pic: String,
    val overview: String
)

object Constant{
    val item = 0
    val Loading = 1
}
class MainActivity : AppCompatActivity() {
    lateinit var result: String
    val datalist = ArrayList<Item>()
    private var isloading : Boolean = false
    lateinit var layoutManager : LinearLayoutManager
    var totaldatalength = 0
    var loading = false
    var receiver = MyReceiver(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val receiverfilter = IntentFilter("MyMessage")
        registerReceiver(receiver, receiverfilter)

        CoroutineScope(Dispatchers.IO).launch {
            result = URL("https://api.themoviedb.org/3/discover/movie?api_key=b774b8444f44b3c5fa4d07f3295fa07b&with_genres=18&primary_release_year=2014").readText()
            val jsonObject_results = JSONObject(result).getJSONArray("results")
            totaldatalength += jsonObject_results.length()
            for(i in 0 until jsonObject_results.length()){
                val obj = jsonObject_results.getJSONObject(i)
                val title = obj.getString("title")
                val poster_path = obj.getString("poster_path")
                val overview = obj.getString("overview")
                datalist.add(Item(title, poster_path, overview))
            }
            withContext(Dispatchers.Main){
                layoutManager = LinearLayoutManager(this@MainActivity)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                listview.layoutManager = layoutManager
                listview.setHasFixedSize(true)
                listview.adapter = DataAdapter(datalist)
            }
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                InstanceIdResult ->
                val token = InstanceIdResult.token
                Log.d("cloudMessage","$token")
            }
        }
        swipe.setColorSchemeColors(Color.BLACK)

        val listener = object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                update()
                listview.adapter?.notifyDataSetChanged()
                swipe.isRefreshing = false
            }
        }
        swipe.setOnRefreshListener(listener)
        addscrollListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun addscrollListener(){
        listview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!loading){
                    if(!recyclerView.canScrollVertically(1)&&newState == RecyclerView.SCROLL_STATE_IDLE){
                        loading = true
                        loadmore()

                    }
                }

            }
        })
    }

    private fun loadmore(){
        var length = 0
        addItem()
        Timer().schedule(3000){
            result = URL("https://api.themoviedb.org/3/discover/movie?primary_release_year=2010&sort_by=vote_average.desc&api_key=b774b8444f44b3c5fa4d07f3295fa07b").readText()
            val jsonObject_results = JSONObject(result).getJSONArray("results")
            length = jsonObject_results.length()
            totaldatalength += length
            removeItem()
            for(i in 0 until jsonObject_results.length()){
                val obj = jsonObject_results.getJSONObject(i)
                val title = obj.getString("title")
                val poster_path = obj.getString("poster_path")
                val overview = obj.getString("overview")
                datalist.add(Item(title, poster_path, overview))
            }
            listview.adapter?.notifyItemRangeChanged(totaldatalength+length, datalist.size)
            loading = false
        }
    }

    private fun addItem(){
        Handler().post(Runnable {
            datalist.add(Item("","",""))
            listview.adapter?.notifyItemInserted(datalist.size -1)
        })
    }

    private fun removeItem(){
        if(datalist.size != 0){
            datalist.removeAt(datalist.size - 1)
            listview.adapter?.notifyItemRemoved(datalist.size)
        }
    }

    fun update() {
        val datalist1 = ArrayList<Item>()
        CoroutineScope(Dispatchers.IO).launch {
            result =
                URL("https://api.themoviedb.org/3/discover/movie?primary_release_year=2010&sort_by=vote_average.desc&api_key=b774b8444f44b3c5fa4d07f3295fa07b").readText()
            val jsonObject_results = JSONObject(result).getJSONArray("results")
            for (i in 0 until jsonObject_results.length()) {
                val obj = jsonObject_results.getJSONObject(i)
                val title = obj.getString("title")
                val poster_path = obj.getString("poster_path")
                val overview = obj.getString("overview")
                datalist1.add(Item(title, poster_path, overview))
            }
            withContext(Dispatchers.Main) {
                val layoutManager = LinearLayoutManager(this@MainActivity)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                listview.layoutManager = layoutManager
                listview.adapter = DataAdapter(datalist1)
            }
        }
    }
}

class DataAdapter(private val mData: ArrayList<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
    class ViewHolder2(v: View) : RecyclerView.ViewHolder(v)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == Constant.item){
            val v = LayoutInflater.from(parent.context).inflate(R.layout.av, parent, false)
            ViewHolder(v)
        }else{
            val v = LayoutInflater.from(parent.context).inflate(R.layout.load, parent, false)
            ViewHolder2(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.item){
            val img_url = "https://image.tmdb.org/t/p/w500" + mData[position].pic
            Picasso.get().load(img_url).into(holder.itemView.iv1)
            holder.itemView.title.text = mData[position].title
            holder.itemView.overview.text = mData[position].overview
            holder.itemView.setOnClickListener {
                Toast.makeText(it.context, "Item ${position+1} is clicked.", Toast.LENGTH_SHORT).show()
            }
            holder.itemView.setOnLongClickListener {
                AlertDialog.Builder(it.context)
                    .setTitle(mData[position].title)
                    .setMessage(mData[position].overview)
                    .setPositiveButton("確認"){dialog, which ->
                        Toast.makeText(it.context, "ok",Toast.LENGTH_SHORT).show()
                    }.show()
                true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(mData[position].pic == ""&&mData[position].overview == ""&&mData[position].title == ""){
            Constant.Loading
        }else{
            Constant.item
        }
    }
    override fun getItemCount(): Int {
        return mData.size
    }
}


