package com.seven.ontracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_privacy.*

class PrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        privacyTextView.movementMethod = ScrollingMovementMethod()
    }
}