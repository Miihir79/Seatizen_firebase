package com.example.seatizen_firebase.Adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seatizen_firebase.R
import kotlinx.android.synthetic.main.row_pattern_report.view.*

class RecyclerAdaptor_report(private var id_bus :ArrayList<String>,private var up_bus:ArrayList<String>,private var down_bus:ArrayList<String>, private var hour_bus:ArrayList<String>):
    RecyclerView.Adapter<RecyclerAdaptor_report.ViewHolder>(){

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val Id : TextView = itemView.Bus_id_rept
            val up : TextView = itemView.Bus_up_rept
            val Down  : TextView = itemView.Bus_down_rept
            val time: TextView = itemView.Bus_hour

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_pattern_admin,parent,false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.Id.text = id_bus[position].toString()
            holder.up.text = up_bus[position].toString()
            holder.Down.text = down_bus[position].toString()
            holder.time.text = hour_bus[position].toString()
        }

        override fun getItemCount(): Int {
            return id_bus.size
        }
}