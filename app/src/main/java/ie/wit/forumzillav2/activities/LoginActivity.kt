package ie.wit.forumzillav2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.main.ForumApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class LoginActivity: AppCompatActivity(), AnkoLogger {

    lateinit var app: ForumApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        app = application as ForumApp
        app.auth = FirebaseAuth.getInstance()
        app.database = FirebaseDatabase.getInstance().reference



        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_register_textview.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this, "Please fill out email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity<Home>()
                } else {
                    Log.d("Login", "Failed ")
                }
            }

    }

}