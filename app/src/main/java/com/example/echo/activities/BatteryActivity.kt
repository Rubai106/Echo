package com.example.echo.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.echo.BatteryPercentage
import com.example.echo.R
import com.example.echo.navigation.SecondaryActivityToolbar

class BatteryActivity : AppCompatActivity() {
    private lateinit var percentageText: TextView
    private var receiverRegistered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_BATTERY_CHANGED) return
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) {
                val percentage = BatteryPercentage.calculate(level, scale)
                percentageText.text = getString(R.string.battery_percentage, percentage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)
        SecondaryActivityToolbar.configure(
            this,
            findViewById<Toolbar>(R.id.secondary_toolbar),
            getString(R.string.battery_title)
        )
        percentageText = findViewById(R.id.battery_percentage_text)
    }

    // ACTION_BATTERY_CHANGED is a sticky system broadcast and must be
    // received through dynamic registration (not a manifest receiver).
    // Scope registration to the visible activity and unregister in onStop().
    override fun onStart() {
        super.onStart()
        if (!receiverRegistered) {
            ContextCompat.registerReceiver(
                this,
                batteryReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                ContextCompat.RECEIVER_EXPORTED
            )
            receiverRegistered = true
        }
    }

    override fun onStop() {
        if (receiverRegistered) {
            unregisterReceiver(batteryReceiver)
            receiverRegistered = false
        }
        super.onStop()
    }
}
