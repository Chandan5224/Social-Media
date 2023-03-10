package com.example.socialmedia20.Adapters

import android.animation.Animator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Adapters.PostAdapter.PostViewHolder
import com.example.socialmedia20.Data.*
import com.example.socialmedia20.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class PostAdapter(
    options: FirestoreRecyclerOptions<Post>,
    private val listener: IPostAdapter,
    private val check: Boolean
) :
    FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
        options
    ) {

    class PostViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        lateinit var postText: TextView
        lateinit var userText: TextView
        lateinit var createdAt: TextView
        lateinit var likeCount: TextView
        lateinit var userImage: ImageView
        lateinit var likeButton: ImageView
        lateinit var image: ImageView
        lateinit var menu: ImageView
        lateinit var shareBtn: ImageView
        lateinit var cmtBtn: ImageView
        lateinit var saveBtn: ImageView
        lateinit var lottieAnimationView: LottieAnimationView

        init {
            postText = itemView.findViewById(R.id.postTitle)
            userText = itemView.findViewById(R.id.userName)
            createdAt = itemView.findViewById(R.id.createdAt)
            likeCount = itemView.findViewById(R.id.likeCount)
            userImage = itemView.findViewById(R.id.userImage)
            likeButton = itemView.findViewById(R.id.likeButton)
            image = itemView.findViewById(R.id.postImage)
            menu = itemView.findViewById(R.id.menus)
            shareBtn = itemView.findViewById(R.id.shareButton)
            cmtBtn = itemView.findViewById(R.id.cmtButton)
            saveBtn = itemView.findViewById(R.id.saveButton)
            lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop()
            .into(holder.userImage)
        Glide.with(holder.image.context).load(model.imageUrl).into(holder.image)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        // check the user already like the post or not
        val auth = Firebase.auth
        val currentUserID = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserID)
        if (isLiked) {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context,
                    R.drawable.ic_liked
                )
            )
        } else {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context,
                    R.drawable.ic_unliked
                )
            )
        }

        holder.saveBtn.setOnClickListener {
            listener.onSave(model)
        }

        val isSave = model.createdBy!!.save.contains(currentUserID)
        if (isSave) {
            holder.saveBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.saveBtn.context,
                    R.drawable.ic_baseline_bookmark_24
                )
            )
        } else {
            holder.saveBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.saveBtn.context,
                    R.drawable.ic_baseline_bookmark_border_24
                )
            )
        }



        holder.cmtBtn.setOnClickListener {
            listener.onComment(model)
        }

        holder.likeButton.setOnClickListener {
            if (!isLiked) {
                holder.lottieAnimationView.visibility = View.VISIBLE
                holder.lottieAnimationView.playAnimation()
            }
            listener.onLikeClicked(snapshots.getSnapshot(holder.adapterPosition).id)
        }


        holder.menu.setOnClickListener {
            listener.onMenu(it, model.uid, model)
        }
        holder.shareBtn.setOnClickListener {
            listener.onSharePost(model, holder.image)
        }

        holder.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                holder.lottieAnimationView.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

        })
    }

    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()
    }
}

interface IPostAdapter {
    fun onLikeClicked(postId: String)
    fun onSharePost(post: Post, imageView: ImageView)
    fun onComment(post: Post)
    fun onSave(post: Post)
    fun onMenu(view: View,postId: String,post: Post)
}




