package com.example.socialmedia20.Adapters

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.Data.Utils
import com.example.socialmedia20.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class PostAdapter(options: FirestoreRecyclerOptions<Post>, private val listener:IPostAdapter) : FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder>(
    options
){

    class PostViewHolder(itemView: android.view.View): RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val image:ImageView=itemView.findViewById(R.id.imageView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder= PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
        viewHolder.likeButton.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {

        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        // check the user already like the post or not
        val auth=Firebase.auth
        val currentUserID=auth.currentUser!!.uid
        val isLiked=model.likedBy.contains(currentUserID)
        val storageRef: StorageReference = FirebaseStorage.getInstance().getReference("images/"+model.uid+".jpg")
        try {
            val localFile:File=File.createTempFile("tempfile",".jpg")
                storageRef.getFile(localFile).addOnSuccessListener {
                    val bitmap=BitmapFactory.decodeFile(localFile.absolutePath)
                    holder.image.setImageBitmap(bitmap)
                    Log.d(TAG, "Post uid"+model.uid)
                }.removeOnCanceledListener {
                    Log.d(TAG, "onBindViewHolder: not found")
                }
        }catch (_:Exception){
            Log.d(TAG, "onBindViewHolder: not found")
        }

//        if(model.imageUrl.isNotEmpty())
//            holder.image.setImageURI(model.imageUrl.toUri())
//        else
//            holder.image.setImageDrawable(ContextCompat.getDrawable(holder.image.context,R.drawable.ic_news))
        if (isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked))
        }else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked))
        }

    }
}

interface IPostAdapter{
    fun onLikeClicked(postId:String)
}



