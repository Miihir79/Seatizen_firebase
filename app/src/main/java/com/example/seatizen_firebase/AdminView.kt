package com.example.seatizen_firebase

import android.app.StatusBarManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_main.*

class AdminView : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view)
        window.statusBarColor = ContextCompat.getColor(this,R.color.statusbar)
        supportActionBar?.hide()
        loadDataAdmin()
    }
    private fun loadDataAdmin(){
        val Bus_count = ArrayList<String>()
        val Bus_ID = ArrayList<String>()
        val Bus_up = ArrayList<String>()
        val Bus_down = ArrayList<String>()
        val Bus_uniqueKey = ArrayList<String>()
        //Bus_count.add("12")
        val ref = FirebaseDatabase.getInstance().getReference("/prototype-pcs-default-rtdb/BusDemo")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value_unique = snapshot.key
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                val value_up = snapshot.child("count_up").getValue<Long>()
                val value_down = snapshot.child("count_down").getValue<Long>()
                if (value_unique != null) {
                    Bus_uniqueKey.add(value_unique)
                }
                if (value_id != null) {
                    //Bus_ID.clear()
                    Bus_ID.add(value_id.toString())
                    Log.i("TAG", "onChildAdded: $Bus_ID")

                }
                if (value_count != null) {
                    // Bus_count.clear()
                    Bus_count.add(value_count.toString())
                    Log.i("TAG", "onChildAdded now: $Bus_count")

                }
                if (value_up != null) {
                    Bus_up.add(value_up.toString())
                    Log.i("TAG", "onChildAdded now: $Bus_up")

                }
                if (value_up != null) {
                    Bus_down.add(value_down.toString())
                    Log.i("TAG", "onChildAdded now: $Bus_down")

                }
                rv_recycler.layoutManager = LinearLayoutManager(this@AdminView)
                rv_recycler.adapter = RecyclerAdaptor_admin(Bus_count,Bus_ID, Bus_up, Bus_down)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                var changedItemNum =0
                for( i in 0 until Bus_uniqueKey.size){
                    if(snapshot.key == Bus_uniqueKey[i]){
                        changedItemNum = i
                    }
                }
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                val value_up = snapshot.child("count_up").getValue<Long>()
                val value_down = snapshot.child("count_down").getValue<Long>()
                if (value_count != null) {
                    if (value_id != null) {
                        Bus_ID[changedItemNum] = value_id.toString()
                        Bus_count[changedItemNum]= value_count.toString()
                        Bus_up[changedItemNum]= value_up.toString()
                        Bus_down[changedItemNum]= value_down.toString()
                    }
                    Log.i("TAG", "onChildchanaged: $Bus_count")

                }

                rv_recycler.layoutManager = LinearLayoutManager(this@AdminView)
                rv_recycler.adapter = RecyclerAdaptor_admin(Bus_count,Bus_ID, Bus_up, Bus_down)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                val value_up = snapshot.child("count_up").getValue<Long>()
                val value_down = snapshot.child("count_down").getValue<Long>()
                if (value_id != null) {

                    Log.i("TAG", "onChildremoved: $Bus_ID")

                }
                if (value_count != null) {
                    Log.i("TAG", "onChildreomved: $Bus_count")

                }
                rv_recycler.layoutManager = LinearLayoutManager(this@AdminView)
                rv_recycler.adapter = RecyclerAdaptor_admin(Bus_count,Bus_ID, Bus_up, Bus_down)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}