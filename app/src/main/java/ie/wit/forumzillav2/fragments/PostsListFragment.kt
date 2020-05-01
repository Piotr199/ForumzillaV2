package ie.wit.forumzillav2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.adapters.PostsAdapter
import ie.wit.forumzillav2.adapters.PostsListener
import ie.wit.forumzillav2.main.ForumApp
import ie.wit.forumzillav2.models.Post
import kotlinx.android.synthetic.main.fragment_posts.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class PostsListFragment : Fragment()  , AnkoLogger, PostsListener {

    lateinit var app: ForumApp
    lateinit var root: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as ForumApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_posts, container, false)
        activity?.title = getString(R.string.homepage)

        getAllPosts()

        root.recyclerView.layoutManager = LinearLayoutManager(activity)
        setSwipeRefresh()

        root.imageButton.setOnClickListener{
            navigateTo(CreatePostFragment.newInstance())
        }
        return root
    }

    private fun view(post: Post) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, ViewPostFragment.newInstance(post))
            .addToBackStack(null)
            .commit()
    }



    fun getAllPosts() {
        val postsList = ArrayList<Post>()
        app.database.child("posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Post error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val post = it.
                        getValue<Post>(Post::class.java)

                        postsList.add(post!!)
                        root.recyclerView.adapter =
                            PostsAdapter(postsList, this@PostsListFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("posts")
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllPosts()
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PostsListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    private fun navigateTo(fragment: Fragment) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onPostClick(post: Post) {
        view(post)
    }

}