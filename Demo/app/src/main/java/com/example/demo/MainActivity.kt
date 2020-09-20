package com.example.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class Item(
    val title: String,
    val pic: String,
    val overview: String
)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val datalist = ArrayList<Item>()
        CoroutineScope(Dispatchers.IO).launch {
            val result = URL("https://api.themoviedb.org/3/discover/movie?api_key=b774b8444f44b3c5fa4d07f3295fa07b&with_genres=18&primary_release_year=2014").readText()
            val jsonObject_results = JSONObject(result).getJSONArray("results")
            for(i in 0 until jsonObject_results.length()){
                val obj = jsonObject_results.getJSONObject(i)
                val title = obj.getString("title")
                val poster_path = obj.getString("poster_path")
                val overview = obj.getString("overview")
                datalist.add(Item(title, poster_path, overview))
            }
            withContext(Dispatchers.Main){
                val layoutManager = LinearLayoutManager(this@MainActivity)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                listview.layoutManager = layoutManager
                listview.adapter = DataAdapter(datalist)
            }
        }
    }
}

class DataAdapter(private val mData: ArrayList<Item>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.av, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val img_url = "https://image.tmdb.org/t/p/w500" + mData[position].pic
        Picasso.get().load(img_url).into(holder.imageView)
        holder.title.text = mData[position].title
        holder.overview.text = mData[position].overview
        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, "Item ${position+1} is clicked.", Toast.LENGTH_SHORT).show()
        }
        holder.itemView.setOnLongClickListener {
            Toast.makeText(it.context, "Item ${position+1} is long clicked.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}

class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val imageView: ImageView = v.findViewById(R.id.iv1)
    val title: TextView = v.findViewById(R.id.title)
    val overview: TextView = v.findViewById(R.id.overview)
}
