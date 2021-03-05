package com.example.seatizen_firebase

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.statusbar)
        LoadData()
        FirebaseApp.initializeApp(this)
        btn_admin.setOnClickListener{
            Pass_admin.visibility = View.VISIBLE
            btn_admin2.visibility = View.VISIBLE
            rv_recycler.visibility = View.INVISIBLE
            btn_admin2.setOnClickListener{
                if (Pass_admin.text.toString() == "1234")
                {
                    Pass_admin.text = null
                    Pass_admin.visibility = View.INVISIBLE
                    btn_admin2.visibility = View.INVISIBLE
                    rv_recycler.visibility = View.VISIBLE
                    val intent = Intent(this,AdminView::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(applicationContext,"Wrong Password",Toast.LENGTH_LONG).show()
                    Pass_admin.visibility = View.INVISIBLE
                    btn_admin2.visibility = View.INVISIBLE
                    rv_recycler.visibility = View.VISIBLE
                }
            }

        }
    }
    private fun LoadData(){
        val Bus_count = ArrayList<String>()
        val Bus_ID = ArrayList<String>()
        val Bus_uniqueKey = ArrayList<String>()
        //Bus_count.add("12")
        val ref = FirebaseDatabase.getInstance().getReference("/prototype-pcs-default-rtdb/BusDemo")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value_unique = snapshot.key
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
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID,Bus_count)
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
                /*if (value_id != null) {
                    //Bus_ID[value_id.toInt()]= value_id.toString()
                    //Bus_ID.removeAt(value_id.toInt())
                   // Bus_ID.add(value_id.toString())
                    Log.i("TAG", "onChildAdded: $Bus_ID")

                }*/

                if (value_count != null) {
                    if (value_id != null) {
                        Bus_ID[changedItemNum] = value_id.toString()
                        Bus_count[changedItemNum]= value_count.toString()
                    }
                    //value_id?.toInt()?.let { Bus_count.removeAt(it) }
                    //Bus_count.add(value_count.toString())
                    Log.i("TAG", "onChildchanaged: $Bus_count")

                }

                rv_recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID,Bus_count)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
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
                rv_recycler.adapter = RecyclerAdaptor(Bus_ID,Bus_count)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}

