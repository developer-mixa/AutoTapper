package com.example.autotapper.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.showToast(message: String){
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

val Fragment.nestedActivity: AppCompatActivity get() {
    return activity as AppCompatActivity
}