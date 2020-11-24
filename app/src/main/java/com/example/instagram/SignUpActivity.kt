package com.example.instagram

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val signin = findViewById<TextView>(R.id.signin)
        signin.setOnClickListener {
            finish()
        }

        val fullName = findViewById<EditText>(R.id.fullNameSignup)
        val userName = findViewById<EditText>(R.id.userNameSignup)
        val email = findViewById<EditText>(R.id.emailSignup)
        val password = findViewById<EditText>(R.id.passwordSignup)
        val signupBtn = findViewById<TextView>(R.id.signupBtn)

        signupBtn.setOnClickListener {
            val a = fullName.text.toString()
            val b = userName.text.toString()
            val c = email.text.toString()
            val d = password.text.toString()

            when{
               TextUtils.isEmpty(a) -> Toast.makeText(this, "Full Name Empty", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(b) -> Toast.makeText(this, "User Name Empty", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(c) -> Toast.makeText(this, "Email Empty", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(d) -> Toast.makeText(this, "Password Empty", Toast.LENGTH_SHORT).show()
                else -> {
                    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    mAuth.createUserWithEmailAndPassword(c,d).addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            saveUser(a,b,c)
                        }else{
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


    }

    private fun saveUser(a: String, b: String, c: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userReff: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = a.toLowerCase()
        userMap["username"] = b.toLowerCase()
        userMap["email"] = c
        userMap["bio"] = "Hello World"
        userMap["image"] = "https://i.redd.it/vqfe518ghnn31.jpg"

        userReff.child(currentUserId).setValue(userMap).addOnCompleteListener {task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                FirebaseDatabase.getInstance().reference
                        .child("Follow")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("Following")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(true)

                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else{
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Final Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}