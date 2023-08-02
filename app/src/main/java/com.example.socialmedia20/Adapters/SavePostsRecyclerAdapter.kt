package com.example.socialmedia20.Adapters

import android.animation.Animator
import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.Data.Utils
import com.example.socialmedia20.NewsAdapter
import com.example.socialmedia20.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class SavePostsRecyclerAdapter(
    val context: Context,
    private val postList: List<Post>,
    private val listener: OnClickSavePost
) : RecyclerView.Adapter<SavePostsRecyclerAdapter.SavePostsViewHolder>() {

    class SavePostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        var shareCount: TextView = itemView.findViewById(R.id.shareCount)
        var comCount: TextView = itemView.findViewById(R.id.comCount)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val image: ImageView = itemView.findViewById(R.id.postImage)
        val menu: ImageView = itemView.findViewById(R.id.menus)
        val shareBtn: ImageView = itemView.findViewById(R.id.shareButton)
        val cmtBtn: ImageView = itemView.findViewById(R.id.cmtButton)
        val saveBtn: ImageView = itemView.findViewById(R.id.saveButton)
        val lottieAnimationView: LottieAnimationView =
            itemView.findViewById(R.id.lottieAnimaationView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavePostsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return SavePostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavePostsViewHolder, position: Int) {
        val model = postList[position]

        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop()
            .into(holder.userImage)
        Glide.with(holder.image.context).load(model.imageUrl).into(holder.image)
        holder.likeCount.text = Utils.formatCount(model.likedBy.size.toLong())
        holder.shareCount.text = Utils.formatCount(model.shareBy.size.toLong())
        holder.comCount.text = Utils.formatCount(model.comments.size.toLong())
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        holder.saveBtn.setImageDrawable(
            ContextCompat.getDrawable(
                holder.saveBtn.context,
                R.drawable.ic_baseline_delete_outline_24
            )
        )
        holder.saveBtn.setOnClickListener {
            listener.delete(model)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

}

interface OnClickSavePost {
    fun delete(post: Post)
}