package com.example.socialmedia20.Adapters

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
import com.bumptech.glide.Glide
import com.example.socialmedia20.Data.Post
import com.example.socialmedia20.Data.PostDao
import com.example.socialmedia20.Data.Utils
import com.example.socialmedia20.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder


class PostAdapter(options: FirestoreRecyclerOptions<Post>, private val listener: IPostAdapter) :
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

        init {
            postText = itemView.findViewById(R.id.postTitle)
            userText = itemView.findViewById(R.id.userName)
            createdAt = itemView.findViewById(R.id.createdAt)
            likeCount = itemView.findViewById(R.id.likeCount)
            userImage = itemView.findViewById(R.id.userImage)
            likeButton = itemView.findViewById(R.id.likeButton)
            image = itemView.findViewById(R.id.imageView2)
            menu = itemView.findViewById(R.id.menus)
            shareBtn = itemView.findViewById(R.id.shareButton)
            cmtBtn = itemView.findViewById(R.id.cmtButton)
        }
    }

    private fun popupMenus(view: View, postId: String, post: Post) {
        val popupMenus = PopupMenu(view.context, view)
        popupMenus.inflate(R.menu.post_menu)

        popupMenus.setOnMenuItemClickListener {

            val auth = Firebase.auth
            val postDao = PostDao()

            when (it.itemId) {
                R.id.editBtn -> {
                    if (auth.currentUser!!.uid == post.createdBy.uid) {
                        val dialogPlus = DialogPlus.newDialog(view.context)
                            .setContentHolder(ViewHolder(R.layout.dialog_plus))
                            .setExpanded(true, 1300)
                            .setCancelable(false)
                            .create()

                        val view = dialogPlus.holderView
                        val image = view.findViewById<ImageView>(R.id.editImage)
                        val text = view.findViewById<EditText>(R.id.editTitle)
                        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
                        val title = view.findViewById<TextView>(R.id.title_)

                        title.text = "Edit Post"
                        text.setText(post.text)
                        Glide.with(view.context).load(post.imageUrl).into(image)

                        dialogPlus.show()

                        saveBtn.setOnClickListener {
                            postDao.updatePost(text.text.toString(), post.imageUrl, postId)
                            dialogPlus.dismiss()
                        }
                    } else {
                        Toast.makeText(
                            view.context,
                            "You can't modify others posts!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }
                R.id.deleteBtn -> {
                    if (auth.currentUser!!.uid == post.createdBy.uid) {
                        val postDao = PostDao()
                        postDao.deletePost(postId)
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            view.context,
                            "You can't modify others posts!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }
                else -> true
            }
        }
        popupMenus.show()
      // Menu buttons icon
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(menu, true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
        viewHolder.likeButton.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        return viewHolder
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
        holder.menu.setOnClickListener {
            popupMenus(it, snapshots.getSnapshot(holder.adapterPosition).id, model)
        }

    }

    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()
    }
}

interface IPostAdapter {
    fun onLikeClicked(postId: String)
}




