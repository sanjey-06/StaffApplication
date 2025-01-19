package com.example.staffapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class StaffRoomAdapter(
    private val context: Context,
    private val roomStatusList: List<Map<String, Any>>
) : BaseAdapter() {

    override fun getCount(): Int {
        return roomStatusList.size
    }

    override fun getItem(position: Int): Any {
        return roomStatusList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            android.R.layout.simple_list_item_2,
            parent,
            false
        )

        val roomData = roomStatusList[position]
        val roomName = roomData["name"].toString()
        val roomStatus = roomData["status"].toString()

        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)

        text1.text = roomName
        text2.text = roomStatus

        // Add padding to each item in the spinner
        text1.setPadding(16, 16, 16, 16)  // Horizontal and vertical padding
        text2.setPadding(16, 16, 16, 16)  // Horizontal and vertical padding

        return view
    }
}
