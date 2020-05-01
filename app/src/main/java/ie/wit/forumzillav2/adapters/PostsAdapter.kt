package ie.wit.forumzillav2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.models.Post
import kotlinx.android.synthetic.main.card_post.view.*

interface PostsListener {
    fun onPostClick(post: Post)
}
class PostsAdapter constructor(var posts: ArrayList<Post>,
                               private val listener: PostsListener) : RecyclerView.Adapter<PostsAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(
                parent?.context
            ).inflate(R.layout.card_post, parent, false)
        )

    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val post = posts[holder.adapterPosition]
        holder.bind(post, listener)
    }

    override fun getItemCount(): Int = posts.size

    fun removeAt(position: Int) {
        posts.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(post: Post, listener: PostsListener) {
            itemView.tag = post
            itemView.txtTitle.text = post.postTitle
            itemView.txtContent.text = post.postContent
            itemView.txtLikes.text = post.likes.toString()

            if(post.isFavourite) itemView.imagefavourite.setImageResource(R.drawable.ic_stars_black_24dp)

            itemView.setOnClickListener {
                listener.onPostClick(post)
            }
        }
    }
}