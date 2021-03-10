package com.example.seatizen_firebase

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seatizen_firebase.Adaptors.RecyclerAdaptor_report
import kotlinx.android.synthetic.main.activity_report.*

class Report : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.statusbar)
        //declarations
        val Bus_hour = ArrayList<String>()
        val Bus_ID = ArrayList<String>()
        val Bus_up = ArrayList<String>()
        val Bus_down = ArrayList<String>()

        //recycler view binding
        rv_recycler_report.layoutManager = LinearLayoutManager(this@Report)
        rv_recycler_report.adapter = RecyclerAdaptor_report(Bus_ID,Bus_up,Bus_down,Bus_hour)
    }
}