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

    private fun authenticateStaff(staffId: String, staffPassword: String) {
        try {
            // Retrieve the staff information from Firebase
            database.child(staffId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val dbPassword = snapshot.child("password").value?.toString() ?: ""

                    if (staffPassword == dbPassword) {
                        // Proceed with login if passwords match
                        val intent = Intent(this, StaffPage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Show invalid password message
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Staff ID not found
                    Toast.makeText(this, "Staff ID not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                // Handle failure cases
                Toast.makeText(this, "Failed to authenticate: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Catch any exceptions that may occur during the authentication process
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
