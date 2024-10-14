package com.example.contacts.presentation.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ContactDTO (val name:String,var number:String):Parcelable{

}