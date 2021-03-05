package com.example.seatizen_firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_pattern.view.Bus_id
import kotlinx.android.synthetic.main.row_pattern.view.Bus_count
import kotlinx.android.synthetic.main.row_pattern_admin.view.*

class RecyclerAdaptor_admin(private var id_bus :ArrayList<String>, private var count_bus:ArrayList<String>,private var up_bus:ArrayList<String>,private var down_bus:ArrayList<String>):
    RecyclerView.Adapter<RecyclerAdaptor_admin.ViewHolder>(){

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val Id : TextView = itemView.Bus_count
        val Count : TextView = itemView.Bus_id
        val Up : TextView = itemView.Bus_up
        val Down : TextView = itemView.Bus_down

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_pattern_admin,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.Id.text = id_bus[position].toString()
        holder.Count.text = count_bus[position].toString()
        holder.Up.text = up_bus[position].toString()
        holder.Down.text = down_bus[position].toString()
    }

    override fun getItemCount(): Int {
        return id_bus.size
    }
}