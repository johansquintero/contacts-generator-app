package com.example.contacts.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contactsgenerator.R

class MainActivity : AppCompatActivity() {

    private val USERNAME: String = "johan"
    private val PASSWORD: String = "12345"

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnLogIn = findViewById<Button>(R.id.btnLogIn)
        btnLogIn.setOnClickListener {validateUser()}
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun validateUser() {
        val usernameInput = findViewById<AppCompatEditText>(R.id.usernameInput)
        val passwordInput = findViewById<AppCompatEditText>(R.id.passwordInput)
        val username: String = usernameInput.text.toString().lowercase()
        val password: String = passwordInput.text.toString()

        if (username != USERNAME) {
            usernameInput.setBackgroundDrawable(getDrawable(R.drawable.red_border))
        } else {
            usernameInput.setBackgroundDrawable(getDrawable(R.drawable.normal_border))
        }
        if (password != PASSWORD) {
            passwordInput.setBackgroundDrawable(getDrawable(R.drawable.red_border))
        } else {
            passwordInput.setBackgroundDrawable(getDrawable(R.drawable.normal_border))
        }

        if (username.length<10 && username.length>0) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

    }
}