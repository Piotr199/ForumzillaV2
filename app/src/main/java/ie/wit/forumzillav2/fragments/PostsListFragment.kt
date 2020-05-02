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
import ie.wit.forumzillav2.utils.SwipeToDeleteCallback
import ie.wit.forumzillav2.utils.SwipeToEditCallback
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

        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var postTemp = viewHolder.itemView.tag as Post
                if (postTemp.author == app.auth.currentUser!!.email) {
                    val adapter = root.recyclerView.adapter as PostsAdapter
                    adapter.removeAt(viewHolder.adapterPosition)
                    deletePost((viewHolder.itemView.tag as Post).postID)
                    deleteUserPost(
                        app.auth.currentUser!!.uid,
                        (viewHolder.itemView.tag as Post).postID
                    )
                } else {
                    val toast =
                        Toast.makeText(
                            activity!!.applicationContext,
                            "You Dont Own This Post!",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                    getAllPosts()
                }

            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                var postTemp = viewHolder.itemView.tag as Post
                if (postTemp.author == app.auth.currentUser!!.email){
                    edit(viewHolder.itemView.tag as Post)
                } else {
                    val toast =
                        Toast.makeText(
                            activity!!.applicationContext,
                            "You Dont Own This Post!",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                    getAllPosts()
                }
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)


        root.imageButton.setOnClickListener{
            navigateTo(CreatePostFragment.newInstance())
        }
        return root
    }

    fun deletePost(uid: String?) {
        app.database.child("posts").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Post error : ${error.message}")
                    }
                })
    }

    private fun edit(post: Post) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, EditPostsFragment.newInstance(post))
            .addToBackStack(null)
            .commit()
    }

    private fun view(post: Post) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, ViewPostFragment.newInstance(post))
            .addToBackStack(null)
            .commit()
    }

    fun deleteUserPost(userId: String, uid: String?) {
        app.database.child("user-posts").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Post error : ${error.message}")
                    }
                })
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