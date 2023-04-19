package com.example.socialmedia20.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Data.News
import com.example.socialmedia20.R

class SaveNewsRecyclerAdapter(
    val context: Context,
    val newsList: List<News>,
    private val listener: SaveItemClicked
) :
    RecyclerView.Adapter<SaveNewsRecyclerAdapter.SaveViewHolder>() {
    class SaveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val author: TextView = itemView.findViewById(R.id.authorTitle)
        val image: ImageView = itemView.findViewById(R.id.imageItem)
        val time: TextView = itemView.findViewById(R.id.time)
        val shareBtn: ImageView = itemView.findViewById(R.id.shareB)
        val deleteBtn: ImageView = itemView.findViewById(R.id.saveBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news, parent, false)
        val viewHolder = SaveViewHolder(view)
        view.setOnClickListener {
            listener.onItemClicked(newsList[viewHolder.adapterPosition])
        }
        viewHolder.shareBtn.setOnClickListener {
            listener.onShareClick(newsList[viewHolder.adapterPosition], viewHolder.image)
        }

        viewHolder.deleteBtn.setOnClickListener {
            listener.onDeleteClick(newsList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SaveViewHolder, position: Int) {
        val news = newsList[position]

        holder.title.text = news.title
        holder.time.text = news.time
        holder.author.text = news.author
        Glide.with(context).load(news.imageUrl).error(R.drawable.error_news).into(holder.image)
        holder.deleteBtn.setImageDrawable(
            ContextCompat.getDrawable(
                holder.deleteBtn.context,
                R.drawable.ic_baseline_delete_outline_24
            )
        )
    }


    override fun getItemCount(): Int {
        return newsList.size
    }
}

interface SaveItemClicked {
    fun onItemClicked(item: News)
    fun onShareClick(item: News, imageView: ImageView)
    fun onDeleteClick(item: News)
}
