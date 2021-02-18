package com.example.seatizen_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        LoadData()
        FirebaseApp.initializeApp(this)
    }
    private fun LoadData(){
        val Bus_count = ArrayList<String>()
        val Bus_ID = ArrayList<String>()
        //Bus_count.add("12")
        val ref = FirebaseDatabase.getInstance().getReference("/prototype-pcs-default-rtdb/BusDemo")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                if (value_id != null) {
                    Bus_ID.clear()
                    Bus_ID.add(value_id.toString())
                    Log.i("TAG", "onChildAdded: $Bus_ID")

                }
                if (value_count != null) {
                    Bus_count.clear()
                    Bus_count.add(value_count.toString())
                    Log.i("TAG", "onChildAdded now: $Bus_count")

                }
                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID,Bus_count)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                if (value_id != null) {
                    Bus_ID.clear()
                    Bus_ID.add(value_id.toString())
                    Log.i("TAG", "onChildAdded: $Bus_ID")

                }
                if (value_count != null) {
                    Bus_count.clear()
                    Bus_count.add(value_count.toString())
                    Log.i("TAG", "onChildAdded now: $Bus_count")

                }

                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID,Bus_count)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                if (value_id != null) {
                    Bus_ID.clear()
                    Bus_ID.add(value_id.toString())
                    Log.i("TAG", "onChildAdded: $Bus_ID")

                }
                if (value_count != null) {
                    Bus_count.clear()
                    Bus_count.add(value_count.toString())
                    Log.i("TAG", "onChildAdded now: $Bus_count")

                }
                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID,Bus_count)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}

