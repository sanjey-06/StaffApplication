package com.example.staffapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    // Firebase Database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("staff")

        // Find views by ID
        val staffIdEditText: EditText = findViewById(R.id.staffidInput)
        val passwordEditText: EditText = findViewById(R.id.passwordstaffInput)
        val createStaffButton: Button = findViewById(R.id.createButton)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set the default selected item in BottomNavigationView to "Home"
        bottomNavigationView.selectedItemId = R.id.navigation_home

        // Set OnClickListener for the Create Staff Button
        createStaffButton.setOnClickListener {
            val staffId = staffIdEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (staffId.isNotEmpty() && password.isNotEmpty()) {
                // Save the staff data to Firebase
                saveStaffData(staffId, password)
            } else {
                // Show a toast message for incomplete input
                Toast.makeText(this, "Please enter Staff ID and Password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Bottom Navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Stay on the current page (MainActivity)
                    Log.d("MainActivity", "Home selected")
                    true
                }
                R.id.navigation_details -> {
                    // Navigate to the DetailsActivity
                    Log.d("MainActivity", "Details selected, navigating to DetailsActivity")
                    try {
                        val intent = Intent(this, DetailsActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish() // Close MainActivity
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error navigating to DetailsActivity: ${e.message}")
                        Toast.makeText(this, "Failed to navigate: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    // Function to save staff data to Firebase
    private fun saveStaffData(staffId: String, password: String) {
        val staffData = mapOf(
            "staffId" to staffId,
            "password" to password
        )

        // Save the staff data under the staffId in the database
        database.child(staffId).setValue(staffData)
            .addOnSuccessListener {
                // Show success message
                Toast.makeText(this, "Staff Created Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Show error message
                Toast.makeText(this, "Failed to create staff: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
