package com.example.staffapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class DetailsActivity : AppCompatActivity() {

    // Firebase Database reference
    private lateinit var database: DatabaseReference

    // Views
    private lateinit var userDetailsTextView: TextView
    private lateinit var userInfoButton: Button

    // Flag to track whether user info is currently visible
    private var isUserInfoVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailapage)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("users")

        // Find views by ID
        userDetailsTextView = findViewById(R.id.userDetailsTextView)
        userInfoButton = findViewById(R.id.userinfoButton)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set the default selected item in BottomNavigationView to "Details"
        bottomNavigationView.selectedItemId = R.id.navigation_details

        // Set the click listener for the "Users Info" button
        userInfoButton.setOnClickListener {
            if (isUserInfoVisible) {
                userDetailsTextView.visibility = View.GONE
            } else {
                userDetailsTextView.visibility = View.VISIBLE
                fetchAllUserDetails()
            }
            isUserInfoVisible = !isUserInfoVisible
        }

        // Handle Bottom Navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Navigate to the MainActivity
                    Log.d("DetailsActivity", "Home selected, navigating to MainActivity")
                    try {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish() // Close DetailsActivity
                    } catch (e: Exception) {
                        Log.e("DetailsActivity", "Error navigating to MainActivity: ${e.message}")
                        Toast.makeText(this, "Failed to navigate: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.navigation_details -> {
                    // Stay on the current page (DetailsActivity)
                    Log.d("DetailsActivity", "Details selected")
                    true
                }
                else -> false
            }
        }
    }

    // Function to fetch all user details from Firebase
    private fun fetchAllUserDetails() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val stringBuilder = StringBuilder()
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").value.toString()
                        val firstName = userSnapshot.child("firstName").value.toString()
                        val lastName = userSnapshot.child("lastName").value.toString()
                        val mobileNumber = userSnapshot.child("mobileNumber").value.toString()

                        stringBuilder.append("""
                            Email: $email
                            First Name: $firstName
                            Last Name: $lastName
                            Mobile Number: $mobileNumber
                            -------------------------------
                            
                        """.trimIndent()).append("\n")
                    }
                    userDetailsTextView.text = stringBuilder.toString()
                } else {
                    Toast.makeText(this@DetailsActivity, "No users found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailsActivity, "Failed to load user details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
