package com.example.instagram

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.example.instagram.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var imageEdit: ImageView
    private lateinit var update: ImageView
    private lateinit var nameEdit: EditText
    private lateinit var bioEdit: EditText
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val close = findViewById<ImageView>(R.id.close)
        val logout = findViewById<Button>(R.id.logout)
        imageEdit = findViewById(R.id.imageEdit)
        nameEdit = findViewById(R.id.nameEdit)
        update = findViewById(R.id.update)
        bioEdit = findViewById(R.id.bioEdit)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        close.setOnClickListener{
            finish()
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        userInfo()
        update.setOnClickListener {
            if (checker == "clicked"){

            }else{
                updateUserInfoOnly()
            }
        }
    }

    private fun updateUserInfoOnly() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        when{
            TextUtils.isEmpty(nameEdit.toString()) -> Toast.makeText(this, "Full Name Empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(bioEdit.toString()) -> Toast.makeText(this, "Bio Empty", Toast.LENGTH_SHORT).show()
            else -> {
                val userMap = HashMap<String, Any>()

                userMap["fullname"] = nameEdit.text.toString().toLowerCase()
                userMap["bio"] = bioEdit.text.toString()

                userRef.updateChildren(userMap).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "Account Updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Glide.with(imageEdit).load(user!!.getImage()).into(imageEdit)
                    nameEdit.setText(user!!.getFullName())
                    bioEdit.setText(user!!.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}