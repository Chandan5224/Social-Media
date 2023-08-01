package com.example.socialmedia20.Adapters

import android.animation.Animator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.socialmedia20.Data.*
import com.example.socialmedia20.R
import com.example.socialmedia20.RoomDatabase.PostDatabase
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class PostAdapter(
    options: FirestoreRecyclerOptions<Post>,
    private val listener: IPostAdapter,
    private val context: Context
) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
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
            lottieAnimationView = itemView.findViewById(R.id.lottieAnimaationView)
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
        holder.likeCount.text = Utils.formatCount(model.likedBy.size)
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        // check the user already like the post or not
        val auth = Firebase.auth
        val currentUserID = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserID)
        if (isLiked) {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context, R.drawable.ic_liked
                )
            )
        } else {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context, R.drawable.ic_unliked
                )
            )
        }

        holder.saveBtn.setOnClickListener {
            listener.onSave(model)
        }

        /// For Clicked Items

        holder.saveBtn.setOnClickListener {
            if (!PostDbAsyncTask(context, model, 1).execute().get()) {
                model.saveTime = System.currentTimeMillis()
                val async = PostDbAsyncTask(context, model, 2).execute()
                if (async.get()) {
                    holder.saveBtn.setImageDrawable(
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_baseline_bookmark_24
                        )
                    )
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = PostDbAsyncTask(context, model, 3).execute()
                if (async.get()) {
                    holder.saveBtn.setImageDrawable(
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_baseline_bookmark_border_24
                        )
                    )
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //// is save checking
        val isSave = PostDbAsyncTask(context, model, 1).execute()
        if (isSave.get()) {
            holder.saveBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context, R.drawable.ic_baseline_bookmark_24
                )
            )
        } else {
            holder.saveBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context, R.drawable.ic_baseline_bookmark_border_24
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
            if (holder.image.drawable != null)
                listener.onSharePost(model, holder.image)
        }

        holder.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                holder.lottieAnimationView.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })
    }

    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()
    }

    class PostDbAsyncTask(val context: Context, val post: Post, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = PostDatabase.getDatabase(context)
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    //check is save or not
                    val post: Post? = db.postDao().getById(post.uid)
                    return post != null
                }
                2 -> {
                    // insert data
                    db.postDao().addPost(post)
                    return true
                }
                3 -> {
                    //  delete
                    db.postDao().deletePost(post)
                    return true
                }
            }
            return false
        }

    }

}

interface IPostAdapter {
    fun onLikeClicked(postId: String)
    fun onSharePost(post: Post, imageView: ImageView)
    fun onComment(post: Post)
    fun onSave(post: Post)
    fun onMenu(view: View, postId: String, post: Post)
}




