package com.example.socialmedia20

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Data.News
import com.example.socialmedia20.RoomDatabase.NewsDatabase


class NewsAdapter(private val listener: NewsItemClicked, val context: Context) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    var items: ArrayList<News> = ArrayList()

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news, parent, false)
        val viewHolder = NewsViewHolder(view)

        /// For Clicked Items

        viewHolder.itemView.findViewById<ImageView>(R.id.saveBtn).setOnClickListener {
            if (!DbAsyncTask(context, items[viewHolder.adapterPosition], 1).execute().get()) {
                val async = DbAsyncTask(context, items[viewHolder.adapterPosition], 2).execute()
                if (async.get()) {
                    viewHolder.itemView.findViewById<ImageView>(R.id.saveBtn).setImageDrawable(
                        ContextCompat.getDrawable(
                            viewHolder.itemView.context,
                            R.drawable.ic_baseline_bookmark_24
                        )
                    )
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DbAsyncTask(context, items[viewHolder.adapterPosition], 3).execute()
                if (async.get()) {
                    viewHolder.itemView.findViewById<ImageView>(R.id.saveBtn).setImageDrawable(
                        ContextCompat.getDrawable(
                            viewHolder.itemView.context,
                            R.drawable.ic_baseline_bookmark_border_24
                        )
                    )
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show()
                }
            }
        }



        view.setOnClickListener {
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }

        view.findViewById<ImageView>(R.id.shareB).setOnClickListener {
            listener.onShareClick(
                items[viewHolder.adapterPosition],
                view.findViewById<ImageView>(R.id.imageItem)
            )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        holder.itemView.findViewById<TextView>(R.id.newsTitle).text = currentItem.title
        holder.itemView.findViewById<TextView>(R.id.authorTitle).text = currentItem.author
        holder.itemView.findViewById<TextView>(R.id.time).text = currentItem.time
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).error(R.drawable.error_news)
            .into(holder.itemView.findViewById<ImageView>(R.id.imageItem))

        //// is save checking
        val isSave = DbAsyncTask(context, currentItem, 1).execute()
        if (isSave.get()) {
            holder.itemView.findViewById<ImageView>(R.id.saveBtn).setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_baseline_bookmark_24
                )
            )
        } else {
            holder.itemView.findViewById<ImageView>(R.id.saveBtn).setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_baseline_bookmark_border_24
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateNews(updatedNews: ArrayList<News>) {
        items.clear()
        items.addAll(updatedNews)

        ///it's call again whole Adapter work
        notifyDataSetChanged()
    }

    class DbAsyncTask(val context: Context, val news: News, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = NewsDatabase.getDatabase(context)
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    //check is save or not
                    val news: News? = db.newsDao().getById(news.title)
                    return news != null
                }
                2 -> {
                    // insert data
                    db.newsDao().addNews(news)
                    return true
                }
                3 -> {
                    //  delete
                    db.newsDao().deleteNews(news)
                    return true
                }
            }
            return false
        }

    }

}

interface NewsItemClicked {
    fun onItemClicked(item: News)
    fun onShareClick(item: News, imageView: ImageView)
}
