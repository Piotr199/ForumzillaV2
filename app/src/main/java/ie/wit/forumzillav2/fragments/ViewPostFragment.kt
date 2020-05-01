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

        arguments?.let {
            viewpost = it.getParcelable("viewpost")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_view_post, container, false)
        activity?.title = getString(R.string.viewpage)

        root.viewTitle.setText(viewpost!!.postTitle)
        root.viewContent.setText(viewpost!!.postCategory)
        root.viewCategory.setText(viewpost!!.postContent)
        root.txtLikes.setText(viewpost!!.likes.toString())

        if(viewpost!!.isFavourite){
            root.favStatus.setText("Added To Favourites!")
        } else {
            root.favStatus.setText(" ")
        }

        root.imageView2.setOnClickListener {
            viewpost!!.likes = viewpost!!.likes + 1
            updatePost(viewpost!!.postID,viewpost!!)
            updateUserPost(app.auth.currentUser!!.uid,viewpost!!.postID,viewpost!!)
        }

        root.btnAddToFavs.setOnClickListener {
            viewpost!!.isFavourite = true
            updatePost(viewpost!!.postID,viewpost!!)
            updateUserPost(app.auth.currentUser!!.uid,viewpost!!.postID,viewpost!!)
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
        fun newInstance(post: Post) =
            ViewPostFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("viewpost",post)
                }
            }
    }
}