package ie.wit.forumzillav2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.main.ForumApp
import ie.wit.forumzillav2.models.Post
import kotlinx.android.synthetic.main.fragment_edit_post.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class EditPostsFragment : Fragment(), AnkoLogger {

    var editPost: Post? = null
    lateinit var app: ForumApp
    lateinit var root: View



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as ForumApp

        arguments?.let {
            editPost = it.getParcelable("editpost")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit_post, container, false)
        activity?.title = getString(R.string.editpage)

        root.editTitle.setText(editPost!!.postTitle)
        root.editCategory.setText(editPost!!.postCategory)
        root.editContent.setText(editPost!!.postContent)


        root.btnEditPost.setOnClickListener {
            if(!(root.editTitle.text.toString() == "" ||
                        root.editContent.text.toString() == "" ||
                        root.editCategory.text.toString() == "")) {
                updatePostData()
                updatePost(editPost!!.postID, editPost!!)
                updateUserPost(
                    app.auth.currentUser!!.uid,
                    editPost!!.postID, editPost!!
                )
            } else {
                val toast =
                    Toast.makeText(
                        activity!!.applicationContext,
                        "You Dont Own This Post!",
                        Toast.LENGTH_LONG
                    )
                toast.show()
            }
        }


        return root
    }

    fun updatePostData() {
        editPost!!.postTitle = root.editTitle.text.toString()
        editPost!!.postContent = root.editContent.text.toString()
        editPost!!.postCategory = root.editCategory.text.toString()
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
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, PostsListFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    companion object {
        @JvmStatic
        fun newInstance(post: Post) =
            EditPostsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editpost",post)
                }
            }
    }
}