package com.example.seatizen_firebase

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seatizen_firebase.Adaptors.RecyclerAdaptor_report
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_report.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter

class Report : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this,R.color.statusbar)

        load24Nodes()

        writeToFile()

    }

    private fun writeToFile() {
        //to ask for permission
        ActivityCompat.requestPermissions(
                this,
                arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
        )

        val filename = "seatizenTest.csv"
        // create a File object for the parent directory
        val wallpaperDirectory = File("/sdcard/Seatizen/")
        // have the object build the directory structure, if needed.
        wallpaperDirectory.mkdirs()
        // create a File object for the output file
        val outputFile = File(wallpaperDirectory, filename)
        // now attach the OutputStream to the file object, instead of a String representation
        try {
            val fos = FileOutputStream(outputFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        // val root: File = Environment.getExternalStorageDirectory()
        val gpxfile = File(wallpaperDirectory, "seatizenTest.csv") //takes path and filename of new file
        val writer = FileWriter(gpxfile)

        val ref = FirebaseDatabase.getInstance().getReference("/DataLog")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data_device = snapshot.child("Device").getValue<Long>()
                val data_capacity = snapshot.child("capacity").getValue<Long>()
                val data_down = snapshot.child("count_down").getValue<Long>()
                val data_up = snapshot.child("count_up").getValue<Long>()
                val data_time = snapshot.child("time").getValue<String>()

                Log.i("TAG", "onChildAdded: $data_device $data_capacity $data_down $data_up $data_time")

                /*if (data_device != null) {
                    writer.append(data_device.toString())
                }
                writer.append(",")
                if (data_capacity != null) {
                    writer.append(data_capacity.toString())
                }
                writer.append(",")
                if (data_up != null) {
                    writer.append(data_up.toString())
                }
                writer.append(",")
                if (data_down != null) {
                    writer.append(data_down.toString())
                }
                *//*writer.append(",")
                if (data_time != null) {
                    writer.append(data_time)
                }*//*
                writer.append("\n")*/
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        writer.append("Emp_Name")
        writer.append(',')
        writer.append("Adress");
        writer.append('\n')
        writer.append("1Emp_Name")
        writer.append(',')
        writer.append("1Adress");
        writer.append('\n')

        writer.flush()
        writer.close()

        val folder = filesDir
        val f = File(folder, "folder_name")
        f.mkdir()
        val file = File(Environment.DIRECTORY_DOWNLOADS, "seatizenTest")
        if(!file.exists()){
            file.mkdir()
        }
    }

    private fun load24Nodes() {

        //declarations
        val Bus_hour = ArrayList<String>()
        val Bus_ID = ArrayList<String>()
        val Bus_up = ArrayList<String>()
        val Bus_down = ArrayList<String>()

        val ref = FirebaseDatabase.getInstance().getReference("/TimeLog01")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val val_id = snapshot.child("Device").getValue<Long>()
                val val_up = snapshot.child("entries").getValue<Long>()
                val val_down = snapshot.child("exits").getValue<Long>()
                val val_hour = snapshot.child("time").getValue<String>()

                if (val_id != null) { // for null safety
                    Bus_ID.add(val_id.toString())
                }
                if (val_up != null) {
                    Bus_up.add(val_up.toString())
                }
                if (val_down != null) {
                    Bus_down.add(val_down.toString())
                }
                if (val_hour != null) {
                    Bus_hour.add(val_hour.toString())
                }
                Log.i("TAG", "onChildAdded Report: $Bus_ID $Bus_down $Bus_up  $Bus_hour")
                //recycler view binding
                rv_recycler_report.layoutManager = LinearLayoutManager(this@Report)
                rv_recycler_report.adapter = RecyclerAdaptor_report(Bus_ID,Bus_up,Bus_down,Bus_hour)

                //to display graph  refer : https://www.youtube.com/watch?v=kUrmZjUOEyc
                val barentries = ArrayList<BarEntry>()

                for (i in 0 until Bus_up.size){
                    barentries.add(BarEntry(Bus_up[i].toFloat(),i))
                }

                val barDataSet = BarDataSet(barentries,"Passengers")
                barDataSet.color = resources.getColor(R.color.statusbar)

                val data = BarData(Bus_hour,barDataSet)

                barchart.data = data
                //to remove description tag on bottom left corner can customize later
                barchart.setDescription("")

                barchart.setBackgroundColor(resources.getColor(R.color.white))
                barchart.animateXY(3000,3000)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}