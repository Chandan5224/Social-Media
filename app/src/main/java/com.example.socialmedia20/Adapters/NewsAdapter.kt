package com.example.socialmedia20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Data.MainData
import com.example.socialmedia20.Fragments.News

class NewsAdapter(private val listener: NewsItemClicked) :RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    private val items: ArrayList<MainData> = ArrayList()

    class NewsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.news, parent, false)
        val viewHolder=NewsViewHolder(view)

        /// For Clicked Items
        view.setOnClickListener {
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }

        view.findViewById<ImageView>(R.id.shareB).setOnClickListener {
            listener.onShareClick(items[viewHolder.adapterPosition],view.findViewById<ImageView>(R.id.imageItem))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem=items[position]
        holder.itemView.findViewById<TextView>(R.id.itemTitle).text=currentItem.title
        holder.itemView.findViewById<TextView>(R.id.authorTitle).text=currentItem.author
        holder.itemView.findViewById<TextView>(R.id.time).text=currentItem.time
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
    fun onShareClick(item: MainData, imageView: ImageView)
}
