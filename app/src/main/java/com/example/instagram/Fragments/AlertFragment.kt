package com.example.instagram.Fragments

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.NotificationAdapter
import com.example.instagram.Model.Notification
import com.example.instagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class AlertFragment : Fragment() {

    private lateinit var temp: ConstraintLayout
    private lateinit var notifList: RecyclerView
    private var notificationList: List<Notification>? = null
    private var notifAdapter : NotificationAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_alert, container, false)
        temp = view.findViewById(R.id.temp)
        notifList = view.findViewById(R.id.recycler_view_alert)
        notifList?.setHasFixedSize(true)
        notifList?.layoutManager = LinearLayoutManager(context)

        notificationList = ArrayList()
        notifAdapter = NotificationAdapter(context!!, notificationList as ArrayList<Notification>)
        notifList.adapter = notifAdapter

        readNotifications()


        return view
    }

    private fun readNotifications() {
        val notifRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        notifRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                        temp.visibility = View.GONE
                    notifList.visibility = View.VISIBLE
                    (notificationList as ArrayList<Notification>).clear()
                    for (snap in snapshot.children){
                        val notification = snap.getValue(Notification::class.java)
                        (notificationList as ArrayList<Notification>).add(notification!!)
                    }
                    Collections.reverse(notificationList)
                    notifAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}