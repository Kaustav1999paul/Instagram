package com.example.instagram

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        var signup = findViewById<TextView>(R.id.signupNext)
        signup.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        val email = findViewById<EditText>(R.id.emailSignIn)
        val password = findViewById<EditText>(R.id.passwordSignIn)
        val login = findViewById<TextView>(R.id.login)

        login.setOnClickListener {
            val e = email.text.toString()
            val f = password.text.toString()

            when{
                TextUtils.isEmpty(e) -> Toast.makeText(this, "Email empty", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(f) -> Toast.makeText(this, "Password empty", Toast.LENGTH_SHORT).show()
                else -> {
                    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    mAuth.signInWithEmailAndPassword(e,f).addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}