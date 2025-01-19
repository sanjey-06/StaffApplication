package com.example.staffapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class StaffPage : AppCompatActivity() {

    private lateinit var roomSpinner: Spinner
    private lateinit var statusSpinner: Spinner
    private lateinit var updateButton: Button
    private lateinit var viewComplaintsButton: Button
    private lateinit var roomStatusListView: ListView
    private lateinit var complaintsListView: ListView

    private lateinit var database: DatabaseReference

    private val roomList = ArrayList<String>()
    private val roomStatusList = ArrayList<Map<String, Any>>()
    private val complaintsList = ArrayList<Map<String, String>>()

    private var selectedRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.staffpage)

        database = FirebaseDatabase.getInstance().getReference()

        roomSpinner = findViewById(R.id.roomSpinner)
        statusSpinner = findViewById(R.id.statusSpinner)
        updateButton = findViewById(R.id.updateButton)
        viewComplaintsButton = findViewById(R.id.viewComplaintsButton)
        roomStatusListView = findViewById(R.id.roomStatusListView)
        complaintsListView = findViewById(R.id.complaintsListView)

        val statusOptions = arrayOf("Available", "Do Not Disturb", "Cleaning")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        statusSpinner.adapter = statusAdapter

        fetchRooms()
        fetchRoomStatuses()

        updateButton.setOnClickListener {
            selectedRoom = roomSpinner.selectedItem?.toString()
            val selectedStatus = statusSpinner.selectedItem?.toString()

            if (selectedRoom != null && selectedStatus != null) {
                updateRoomStatus(selectedRoom!!, selectedStatus)
            } else {
                Toast.makeText(this, "Please select a room and status", Toast.LENGTH_SHORT).show()
            }
        }

        viewComplaintsButton.setOnClickListener {
            // Toggle visibility of complaints list
            if (complaintsListView.visibility == View.VISIBLE) {
                // If complaints list is visible, hide it
                complaintsListView.visibility = View.GONE
            } else {
                // If complaints list is hidden, fetch and display it
                fetchComplaints()
                complaintsListView.visibility = View.VISIBLE
            }
        }

    }

    private fun fetchRooms() {
        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomList.clear()
                for (roomTypeSnapshot in snapshot.children) {
                    for (roomSnapshot in roomTypeSnapshot.children) {
                        val roomName = roomSnapshot.key
                        if (roomName != null) {
                            roomList.add(roomName)
                        }
                    }
                }

                val roomAdapter = ArrayAdapter(this@StaffPage, android.R.layout.simple_spinner_item, roomList)
                roomSpinner.adapter = roomAdapter

                val selectedIndex = roomList.indexOf(selectedRoom)
                if (selectedIndex >= 0) {
                    roomSpinner.setSelection(selectedIndex)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StaffPage", "Failed to fetch rooms: ${error.message}")
            }
        })
    }

    private fun fetchRoomStatuses() {
        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomStatusList.clear()
                for (roomTypeSnapshot in snapshot.children) {
                    for (roomSnapshot in roomTypeSnapshot.children) {
                        val roomName = roomSnapshot.key ?: "Unknown Room"
                        val roomStatus = roomSnapshot.child("status").value?.toString() ?: "No Status"
                        roomStatusList.add(mapOf("name" to roomName, "status" to roomStatus))
                    }
                }

                updateRoomStatusListView()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StaffPage", "Failed to fetch room statuses: ${error.message}")
            }
        })
    }

    private fun fetchComplaints() {
        database.child("bookings").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintsList.clear()
                for (bookingSnapshot in snapshot.children) {
                    val roomNumber = bookingSnapshot.child("rooms/0/roomId").value.toString()
                    val complaints = bookingSnapshot.child("complaints")
                    for (complaintSnapshot in complaints.children) {
                        val complaintText = complaintSnapshot.child("complaintText").value.toString()
                        complaintsList.add(
                            mapOf(
                                "room" to "Room No $roomNumber",
                                "complaint" to complaintText
                            )
                        )
                    }
                }

                updateComplaintsListView()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StaffPage", "Failed to fetch complaints: ${error.message}")
            }
        })
    }

    private fun updateRoomStatusListView() {
        val adapter = StaffRoomAdapter(this@StaffPage, roomStatusList)
        roomStatusListView.adapter = adapter
    }

    private fun updateComplaintsListView() {
        val adapter = object : BaseAdapter() {
            override fun getCount(): Int {
                return complaintsList.size
            }

            override fun getItem(position: Int): Any {
                return complaintsList[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = convertView ?: LayoutInflater.from(this@StaffPage).inflate(R.layout.dialog_complaints, parent, false)
                val complaintText = view.findViewById<TextView>(R.id.complaintText)
                val complaint = complaintsList[position]
                complaintText.text = "${complaint["room"]} - Complaints: ${complaint["complaint"]}"
                return view
            }
        }

        complaintsListView.adapter = adapter
        complaintsListView.visibility = View.VISIBLE
    }



    private fun updateRoomStatus(room: String, status: String) {
        database.child("rooms").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (roomTypeSnapshot in snapshot.children) {
                    if (roomTypeSnapshot.hasChild(room)) {
                        database.child("rooms/${roomTypeSnapshot.key}/$room/status")
                            .setValue(status)
                        // Show a Toast message indicating success
                        Toast.makeText(this@StaffPage, "Status updated successfully", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StaffPage", "Failed to update room status: ${error.message}")
            }
        })
    }
}
