package com.example.staffapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    // Firebase Database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginpage)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("staff")

        // Find views by ID
        val idEditText: EditText = findViewById(R.id.idInput)
        val passwordEditText: EditText = findViewById(R.id.staffpasswordInput)
        val loginButton: Button = findViewById(R.id.staffloginButton)

        // Set click listener for the login button
        loginButton.setOnClickListener {
            val enteredId = idEditText.text.toString().trim()
            val enteredPassword = passwordEditText.text.toString().trim()

            // Check if fields are not empty
            if (enteredId.isNotEmpty() && enteredPassword.isNotEmpty()) {
                if ((enteredId == "admin1" && enteredPassword == "admin123") ||
                    (enteredId == "admin2" && enteredPassword == "admin456")
                ) {
                    // Redirect to MainActivity for admin credentials
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Check staff credentials dynamically from Firebase
                    authenticateStaff(enteredId, enteredPassword)
                }
            } else {
                // Show error message for empty fields
                Toast.makeText(this, "Please enter ID and Password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to authenticate staff credentials
    private fun authenticateStaff(staffId: String, staffPassword: String) {
        database.child(staffId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val dbStaffId = snapshot.child("staffId").value.toString()
                val dbPassword = snapshot.child("password").value.toString()

                if (staffId == dbStaffId && staffPassword == dbPassword) {
                    // Redirect to StaffPage if authenticated
                    val intent = Intent(this, StaffPage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Show error message if credentials are incorrect
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show error message if staff ID doesn't exist
                Toast.makeText(this, "Staff ID not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Show error message if fetching data fails
            Toast.makeText(this, "Failed to authenticate", Toast.LENGTH_SHORT).show()
        }
    }
}
