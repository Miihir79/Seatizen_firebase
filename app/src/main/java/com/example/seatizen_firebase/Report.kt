package com.example.seatizen_firebase

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.AlarmClock
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Report : AppCompatActivity() {
    val list_Bus = ArrayList<String>()
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this,R.color.statusbar)
        fetchBuses()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{


            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                load24Nodes(list_Bus[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

// for notifications
        /*val intent = Intent(this, ReminderBroadcast::class.java)
        val pending = PendingIntent.getBroadcast(this,0,intent,0)
        val alarm : AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val time : Long = 1000* 20
        alarm.set(AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis() + time,pending)*/

        button.setOnClickListener {
            writeToFile()

        }
        //createNotifChannel()



    }

    private fun fetchBuses() {

        val ref = FirebaseDatabase.getInstance().getReference("TimeLog")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.key?.let { list_Bus.add(it) }
                val arrayAdaptor = ArrayAdapter(this@Report,android.R.layout.simple_spinner_dropdown_item,list_Bus)
                spinner.adapter = arrayAdaptor
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

    /*private fun sendNotif() {

        val builder = NotificationCompat.Builder(this, "01")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Seatizen - Reminder!!")
                .setContentText("Download your report before it is gone")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)){
            notify(101,builder.build())
        }
    }*/

    private fun createNotifChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("01","Reminder!!",NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Download the file for your report"
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeToFile() {
        //to ask for permission
        ActivityCompat.requestPermissions(
                this,
                arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
        )

        val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + File.separator +
                        "Seatizen_Report"
        )
        if (!folder.exists()) {
            Log.d("TAG", "onCreate: " + folder.absolutePath)
            folder.mkdirs()
        }
        val time_start = LocalDateTime.now().toString()
        val time = time_start.substring(0..9)
        Log.i("TAG", "writeToFile: $time")
        val filename = "Seatizen_$time.csv"
    // create a File object for the parent directory
        /* val wallpaperDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
    // have the object build the directory structure, if needed.
         wallpaperDirectory.mkdirs()*/
    // create a File object for the output file
        val outputFile = File(folder, filename)
        Log.d("TAG", "onCreate: " + outputFile.absoluteFile)
    // now attach the OutputStream to the file object, instead of a String representation
        try {
            val fos = FileOutputStream(outputFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val gpxfile = File(folder, filename) //takes path and filename of new file
        val writer = FileWriter(gpxfile)

        writer.append("DeviceId")
        writer.append(",")
        writer.append("Capacity")
        writer.append(",")
        writer.append("CountDown")
        writer.append(",")
        writer.append("CountUp")
        writer.append(",")
        writer.append("Time")
        writer.append(",")
        writer.append("Status")
        writer.append("\n")
        val refrence = FirebaseDatabase.getInstance().getReference("/DataLog")
        refrence.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("TAG", "onChildAddedOuter: ${snapshot.key} ")
                val ref = FirebaseDatabase.getInstance().getReference("/DataLog/${snapshot.key}")
                ref.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                        val data_device = snapshot.child("Device").getValue<Long>()
                        val data_capacity = snapshot.child("capacity").getValue<Long>()
                        val data_down = snapshot.child("count_down").getValue<Long>()
                        val data_status = snapshot.child("status").getValue<String>()
                        val data_up = snapshot.child("count_up").getValue<Long>()
                        val data_time = snapshot.child("time").getValue<String>()

                        writer.append(data_device.toString())
                        writer.append(",")
                        writer.append(data_capacity.toString())
                        writer.append(",")
                        writer.append(data_down.toString())
                        writer.append(",")
                        writer.append(data_up.toString())
                        writer.append(",")
                        writer.append(data_time)
                        writer.append(",")
                        writer.append(data_status)
                        writer.append("\n")
                        writer.flush()

                        /*arrayList1.add(data_device.toString())
                        arrayList2.add(data_capacity.toString())
                        arrayList3.add(data_down.toString())
                        arrayList4.add(data_up.toString())
                        arrayList5.add(data_time.toString())*/

                        Log.i("TAG", "onChildAddedReport: $data_device $data_capacity $data_down $data_up $data_time $data_status")

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

        Toast.makeText(applicationContext,"file successfully created at \n ${gpxfile.absolutePath}",Toast.LENGTH_LONG).show()

        //Log.i("TAG", "writeToFile: $arrayList1 $arrayList3")

       /* for (i in 0 until arrayList1.size) {
            writer.append(arrayList1[i])
            writer.append(",")
            writer.append(arrayList2[i])
            writer.append(",")
            writer.append(arrayList3[i])
            writer.append(",")
            writer.append(arrayList4[i])
            writer.append(",")
            writer.append(arrayList5[i])

            writer.append("\n")
        }*/

        // Request code for selecting a PDF document.
       // openFileOutput(filename,Context.MODE_APPEND)
        //openFile(gpxfile.toUri())
        //openFileInput(filename)

    }


   /* @RequiresApi(Build.VERSION_CODES.O)
    fun openFile(pickerInitialUri: Uri) {

       *//* // Open a specific media item using ParcelFileDescriptor.
        val resolver = applicationContext.contentResolver

// "rw" for read-and-write;
// "rwt" for truncating or overwriting existing file contents.
        val readOnlyMode = "r"
        resolver.openFileDescriptor(pickerInitialUri, readOnlyMode).use { pfd ->
            // Perform operations on "pfd".
        }*//*
    }*/

    private fun load24Nodes(chosen:String) {

        //declarations
        val Bus_hour = ArrayList<String>()
        val Bus_ID = ArrayList<String>()
        val Bus_up = ArrayList<String>()
        val Bus_down = ArrayList<String>()
        val Bus_uniqueKey = ArrayList<String>()
        val ref = FirebaseDatabase.getInstance().getReference("/TimeLog/$chosen")
        ref.addChildEventListener(object : ChildEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val value_unique = snapshot.key
                val val_id = snapshot.child("Device").getValue<Long>()
                val val_up = snapshot.child("entries").getValue<Long>()
                val val_down = snapshot.child("exits").getValue<Long>()
                val val_hour = snapshot.child("time").getValue<String>()

                val time_start = LocalDateTime.now().toString()
                val time = time_start.substring(11..12)

                val timeTocompare = val_hour?.substring(0..1)
                Log.i("To check string", "onChildAdded:$time")
                if (timeTocompare != null) {
                    Log.i("To check ", "onChildAdded:${timeTocompare}")
                }
                if (timeTocompare != null) {
                    if (time.toInt() +1 >= timeTocompare.toInt()) {
                        if (val_id != null) { // for null safety
                            Bus_ID.add(val_id.toString())
                        }
                        if (val_up != null) {
                            Bus_up.add(val_up.toString())
                        }
                        if (val_down != null) {
                            Bus_down.add(val_down.toString())
                        }
                        Bus_hour.add(val_hour.toString())
                    }
                }
                //Log.i("TAG", "onChildAdded Report: $Bus_ID $Bus_down $Bus_up  $Bus_hour")
                //recycler view binding
                rv_recycler_report.layoutManager = LinearLayoutManager(this@Report)
                rv_recycler_report.adapter = RecyclerAdaptor_report(Bus_ID, Bus_up, Bus_down, Bus_hour)

                //to display graph  refer : https://www.youtube.com/watch?v=kUrmZjUOEyc
                val barentries = ArrayList<BarEntry>()

                for (i in 0 until Bus_down.size) {
                    barentries.add(BarEntry(Bus_down[i].toFloat(), i))
                }

                val barDataSet = BarDataSet(barentries, "People going out")
                barDataSet.color = resources.getColor(R.color.statusbar)
                val timeGraph = ArrayList<String>()
                for (i in 0 until Bus_hour.size){
                    timeGraph.add(Bus_hour[i].substring(0..5))
                }
                val data = BarData(timeGraph,barDataSet)

                barchart.data = data
                //to remove description tag on bottom left corner can customize later
                barchart.setDescription("")

                barchart.setBackgroundColor(resources.getColor(R.color.white))
                barchart.animateXY(3000, 3000)

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                var changedItemNum =0
                for( i in 0 until Bus_uniqueKey.size){
                    if(snapshot.key == Bus_uniqueKey[i]){
                        changedItemNum = i
                    }
                }
                val val_id = snapshot.child("Device").getValue<Long>()
                val val_up = snapshot.child("entries").getValue<Long>()
                val val_down = snapshot.child("exits").getValue<Long>()
                val val_hour = snapshot.child("time").getValue<String>()

                val time_start = LocalDateTime.now().toString()
                val time = time_start.substring(11..12)

                val timeTocompare = val_hour?.substring(0..1)
                Log.i("To check string", "onChildAdded:$time")
                if (timeTocompare != null) {
                    Log.i("To check ", "onChildAdded:${timeTocompare}")
                }
                if (timeTocompare != null) {
                    if (time.toInt()+ 1 >= timeTocompare.toInt()) {
                        if (val_id != null) { // for null safety
                            Bus_ID[changedItemNum]= val_id.toString()
                        }
                        if (val_up != null) {
                            Bus_up[changedItemNum]=val_up.toString()
                        }
                        if (val_down != null) {
                            Bus_down[changedItemNum]=val_down.toString()
                        }
                        Bus_hour[changedItemNum]=val_hour.toString()
                    }
                }
                //Log.i("TAG", "onChildAdded Report: $Bus_ID $Bus_down $Bus_up  $Bus_hour")
                //recycler view binding
                rv_recycler_report.layoutManager = LinearLayoutManager(this@Report)
                rv_recycler_report.adapter = RecyclerAdaptor_report(Bus_ID, Bus_up, Bus_down, Bus_hour)

                //to display graph  refer : https://www.youtube.com/watch?v=kUrmZjUOEyc
                val barentries = ArrayList<BarEntry>()

                for (i in 0 until Bus_down.size) {
                    barentries.add(BarEntry(Bus_down[i].toFloat(), i))
                }

                val barDataSet = BarDataSet(barentries, "People going out")
                barDataSet.color = resources.getColor(R.color.statusbar)
                val timeGraph = ArrayList<String>()
                for (i in 0 until Bus_hour.size){
                    timeGraph.add(Bus_hour[i].substring(0..5))
                }
                val data = BarData(timeGraph,barDataSet)

                barchart.data = data
                //to remove description tag on bottom left corner can customize later
                barchart.setDescription("")

                barchart.setBackgroundColor(resources.getColor(R.color.white))
                barchart.animateXY(3000, 3000)


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