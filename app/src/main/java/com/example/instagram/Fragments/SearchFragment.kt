package com.example.instagram.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.UserAdapter
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    private var recycler_view_home : RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null
    lateinit var searchET: EditText
    private var temp: ConstraintLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        temp = view.findViewById(R.id.temp)
        recycler_view_home = view.findViewById(R.id.recycler_view_home)
        recycler_view_home?.setHasFixedSize(true)
        recycler_view_home?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let {
            UserAdapter(it, mUser as ArrayList<User>, true)
        }
        recycler_view_home?.adapter = userAdapter

        searchET = view.findViewById<EditText>(R.id.searchET)
        searchET.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchET.text.toString() == ""){
                    // If textBox is empty then what will happen
                }
                else{
                    // If textBox is not empty then what will happen
                    temp?.visibility = View.GONE
                    recycler_view_home?.visibility = View.VISIBLE
                    retreveUsers()
                   searchUsers(s.toString().toLowerCase())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        return view
    }

    private fun searchUsers(input: String) {
        val query = FirebaseDatabase.getInstance().reference.child("Users")
                .orderByChild("fullname")
                .startAt(input)
                .endAt(input + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()
                for (snapshot in dataSnapshot.children){
                    val user = snapshot.getValue(User::class.java)
                    if (user!=null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



    private fun retreveUsers() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (searchET.text.toString() == ""){
                    // If textBox is empty then what will happen
                    mUser?.clear()
                    for (snapshot in dataSnapshot.children){
                        val user = snapshot.getValue(User::class.java)
                        if (user!=null){
                            mUser?.add(user)
                        }
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}