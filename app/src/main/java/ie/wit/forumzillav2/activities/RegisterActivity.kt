package ie.wit.forumzillav2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d(TAG, "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }


    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                startActivity<LoginActivity>()


                // else if successful
                Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()

                saveUserToFirebaseDatabase()
            }
    }



    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                startActivity<LoginActivity>()
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
            }
    }
}