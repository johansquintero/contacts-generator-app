package com.example.contacts.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contacts.presentation.dto.ContactDTO
import com.example.contactsgenerator.R

class HomeActivity : AppCompatActivity() {

    private var contactList = arrayListOf<ContactDTO>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkPermisions()

        val greetingText = findViewById<TextView>(R.id.greetingText)
        val username: String = intent.extras?.getString("USERNAME").orEmpty()
        val msj: String = "bienvenido\t" + username
        greetingText.setText(msj.uppercase())

        val btnScanContacts = findViewById<CardView>(R.id.cardButtonScan)
        btnScanContacts.setOnClickListener {
            scanContacts()
            val intent = Intent(this, TableActivity::class.java)
            intent.putParcelableArrayListExtra("contactsList", contactList)
            startActivity(intent)
        }
    }

    private fun checkPermisions(){
        val CONTACTS_PERMISSION_CODE: Int = 1
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_CODE
            )
        }
    }

    private fun scanContacts() {
        val CONTACTS_PERMISSION_CODE: Int = 1
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_CODE
            )
        } else {
            getContacts()
        }
    }

    @SuppressLint("Range")
    private fun getContacts() {
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )
                val phoneNumber = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                contactList.add(ContactDTO(name, phoneNumber))
            }
            cursor.close()
            //borra los clonados por numero
            contactList = contactList.distinctBy { it.number } as ArrayList<ContactDTO>
        }
    }
}