package com.example.socialmedia20.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Data.Comment
import com.example.socialmedia20.R

class CommentAdapter(val context: Context) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    var items: ArrayList<Comment> = ArrayList()

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val userIamge = itemView.findViewById<ImageView>(R.id.userImage)
        val comment = itemView.findViewById<TextView>(R.id.comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)

        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.userName.text = items[position].userName
        Glide.with(context).load(items[position].userImage).into(holder.userIamge)
        holder.comment.text = items[position].comment
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateComment(updateList: ArrayList<Comment>) {
        items.clear()
        items.addAll(updateList)
        items.reverse()
        notifyDataSetChanged()
    }

}