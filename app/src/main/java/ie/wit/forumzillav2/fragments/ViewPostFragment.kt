package ie.wit.forumzillav2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.main.ForumApp
import ie.wit.forumzillav2.models.Post
import kotlinx.android.synthetic.main.fragment_view_post.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ViewPostFragment: Fragment(), AnkoLogger {

    var viewpost: Post? = null
    lateinit var app: ForumApp
    lateinit var root: View



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as ForumApp

        arguments?.let { //gets passed in Post
            viewpost = it.getParcelable("viewpost") //puts post to obj
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_view_post, container, false)
        activity?.title = getString(R.string.viewpage)

        root.viewTitle.setText(viewpost!!.postTitle) //fills in details
        root.viewContent.setText(viewpost!!.postContent)
        root.viewCategory.setText(viewpost!!.postCategory)
        root.txtLikes.setText(viewpost!!.likes.toString())

        if(viewpost!!.isFavourite){ //checks if post is favourited
            root.favStatus.setText("Added To Favourites!")
        } else {
            root.favStatus.setText(" ")
        }

        root.imageView2.setOnClickListener { //like button
            viewpost!!.likes = viewpost!!.likes + 1 //when clicked, add 1 like
            updatePost(viewpost!!.postID,viewpost!!) //updates post immidiately
            updateUserPost(app.auth.currentUser!!.uid,viewpost!!.postID,viewpost!!)//updates user-post immidiately
        }

        root.btnAddToFavs.setOnClickListener { //favs button
            viewpost!!.isFavourite = true //sets post.isFav to true
            updatePost(viewpost!!.postID,viewpost!!) //updates post immidiately
            updateUserPost(app.auth.currentUser!!.uid,viewpost!!.postID,viewpost!!)//updates user-post immidiately
            root.favStatus.setText("Added To Favourites!")
        }

        return root
    }

    fun updatePost(uid: String?, post: Post) {
        app.database.child("posts").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(post)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Post error : ${error.message}")
                    }
                })
    }

    fun updateUserPost(userId: String, uid: String?, post: Post) {
        app.database.child("user-posts").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(post)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post) = //passes in a post object
            ViewPostFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("viewpost",post)
                }
            }
    }
}