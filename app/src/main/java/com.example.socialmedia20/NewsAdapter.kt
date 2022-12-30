package com.example.socialmedia20

import android.net.wifi.p2p.WifiP2pManager.NetworkInfoListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(private  val listener: NewsItemClicked) :RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    private val items: ArrayList<MainData> = ArrayList()

    class NewsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.news, parent, false)
        val viewHolder=NewsViewHolder(view)
        view.setOnClickListener {
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem=items[position]
        holder.itemView.findViewById<TextView>(R.id.itemTitle).text=currentItem.title
        holder.itemView.findViewById<TextView>(R.id.authorTitle).text="Author :- "+currentItem.author
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.itemView.findViewById<ImageView>(R.id.imageItem))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateNews(updatedNews: ArrayList<MainData>)
    {
        items.clear()
        items.addAll(updatedNews)

        ///it's call again whole Adapter work
        notifyDataSetChanged()
    }

}
interface NewsItemClicked{
    fun onItemClicked(item : MainData)
}