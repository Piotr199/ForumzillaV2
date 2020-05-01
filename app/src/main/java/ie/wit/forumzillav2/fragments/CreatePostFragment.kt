package ie.wit.forumzillav2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.main.ForumApp
import ie.wit.forumzillav2.models.Post
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_create_post.view.*
import java.util.*

class CreatePostFragment : Fragment() {


    lateinit var app: ForumApp
    lateinit var root: View
    var post = Post()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as ForumApp
        app.database = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_create_post, container, false)
        activity?.title = getString(R.string.createpage)

        root.btnAddPost.setOnClickListener {
            addData()
        }

        return root
    }

    fun addData(){
        post.postTitle = root.addTitle.text.toString()
        post.author = app.auth.currentUser!!.email.toString()
        post.likes = 0
        post.postCategory = root.addCategory.text.toString()
        post.postContent = root.addContent.text.toString()
        post.date = Date().toString()

        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("posts").push().key
        post.postID = key!!

        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/posts/$key"] = postValues
        childUpdates["/user-posts/$uid/$key"] = postValues

        app.database.updateChildren(childUpdates)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreatePostFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}