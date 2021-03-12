package com.example.seatizen_firebase

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seatizen_firebase.Adaptors.RecyclerAdaptor
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        LoadData()

        FirebaseApp.initializeApp(this)

        btn_admin.setOnClickListener{
            //set visibility to make UI cleaner
            Pass_admin.visibility = View.VISIBLE
            btn_admin2.visibility = View.VISIBLE
            rv_recycler.visibility = View.INVISIBLE

            btn_admin2.setOnClickListener{
                if (Pass_admin.text.toString() == "1234") // if password is correct update UI elements for later use and go to AdminView
                {
                    Pass_admin.text = null
                    Pass_admin.visibility = View.INVISIBLE
                    btn_admin2.visibility = View.INVISIBLE
                    rv_recycler.visibility = View.VISIBLE

                    val intent = Intent(this, AdminView::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(applicationContext, "Wrong Password", Toast.LENGTH_LONG).show()
                    //UI elements updated to make it normal (do not remove text of pass_admin so that if user wants to check the password)
                    Pass_admin.visibility = View.INVISIBLE
                    btn_admin2.visibility = View.INVISIBLE
                    rv_recycler.visibility = View.VISIBLE
                }
            }

        }
    }
    //function to fetch data and bind it with UI elements
    private fun LoadData(){
        // ArrayLists to store and manage Data better
        val Bus_count = ArrayList<String>()
        val Bus_ID = ArrayList<String>()
        val Bus_uniqueKey = ArrayList<String>()

        // A reference to the firebase realtime database to access its nodes
        val ref = FirebaseDatabase.getInstance().getReference("/BusDemo")
        ref.addChildEventListener(object : ChildEventListener {
            //When the app starts the children will be 'Added' to the database
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value_unique = snapshot.key // this will help us identify the uniqueness of data and help us update the values
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()

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

                shimmer.stopShimmer() // stops the animation
                shimmer.visibility = View.GONE

                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID, Bus_count)
            }
            //whenever any data is changed this function is invoked
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                var changedItemNum = 0 // to identify which one has changed its value
                for (i in 0 until Bus_uniqueKey.size) {
                    if (snapshot.key == Bus_uniqueKey[i]) {
                        changedItemNum = i
                    }
                }
                //fetching the value
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                /*if (value_id != null) {
                    //Bus_ID[value_id.toInt()]= value_id.toString()
                    //Bus_ID.removeAt(value_id.toInt())
                   // Bus_ID.add(value_id.toString())
                    Log.i("TAG", "onChildAdded: $Bus_ID")

                }*/
                //updating the value at its respective position in both bus_id and bus_count
                if (value_count != null) {
                    if (value_id != null) {
                        Bus_ID[changedItemNum] = value_id.toString()
                        Bus_count[changedItemNum] = value_count.toString()
                    }
                    //value_id?.toInt()?.let { Bus_count.removeAt(it) }
                    //Bus_count.add(value_count.toString())
                    Log.i("TAG", "onChildchanaged: $Bus_count")

                }

                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID, Bus_count)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) { // rare case when this happens the application will blink
                val value_id = snapshot.child("Device").getValue<Long>()
                val value_count = snapshot.child("capacity").getValue<Long>()
                if (value_id != null) {
                    //Bus_ID.clear()

                    Log.i("TAG", "onChildremoved: $Bus_ID")

                }
                if (value_count != null) {
                    //Bus_count.clear()
                    // Bus_count.add(value_count.toString())
                    Log.i("TAG", "onChildreomved: $Bus_count")

                }
                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID, Bus_count)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            } // not needed

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}

