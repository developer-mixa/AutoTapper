package com.example.autotapper.utils

import android.accessibilityservice.AccessibilityService
import android.widget.Toast
import java.security.Provider.Service

fun AccessibilityService.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}