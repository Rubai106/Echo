package com.example.echo.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.echo.BroadcastContract
import com.example.echo.R
import com.example.echo.navigation.SecondaryActivityToolbar

class CustomBroadcastActivity : AppCompatActivity() {
    private lateinit var receivedMessageText: TextView
    private var receiverRegistered = false

    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(BroadcastContract.EXTRA_MESSAGE).orEmpty()
            receivedMessageText.text = message
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_broadcast)
        SecondaryActivityToolbar.configure(
            this,
            findViewById<Toolbar>(R.id.secondary_toolbar),
            getString(R.string.received_message_title)
        )
        receivedMessageText = findViewById(R.id.received_message_text)

        // This is the third activity in the custom-broadcast branch. Register
        // the receiver first, then send the plain-text message as a real app
        // broadcast so onReceive() (not a direct TextView assignment) displays it.
        // RECEIVER_NOT_EXPORTED keeps this app-only custom action private.
        ContextCompat.registerReceiver(
            this,
            customReceiver,
            IntentFilter(BroadcastContract.ACTION_CUSTOM_MESSAGE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        receiverRegistered = true

        val message = intent.getStringExtra(BroadcastContract.EXTRA_MESSAGE).orEmpty()
        sendBroadcast(
            Intent(BroadcastContract.ACTION_CUSTOM_MESSAGE)
                .setPackage(packageName)
                .putExtra(BroadcastContract.EXTRA_MESSAGE, message)
        )
    }

    override fun onDestroy() {
        if (receiverRegistered) {
            unregisterReceiver(customReceiver)
            receiverRegistered = false
        }
        super.onDestroy()
    }
}
