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
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

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
        root = inflater.inflate(R.layout.fragment_home, container, false)
        activity?.title = getString(R.string.homepage)

        val ref = app.database.ref //database reference
        ref.child("posts") //gets everything in firebase/posts
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // get total available quest
                    val size = dataSnapshot.childrenCount.toInt() // counts all child

                    root.postCount.setText(size.toString()) //sets text to size
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}