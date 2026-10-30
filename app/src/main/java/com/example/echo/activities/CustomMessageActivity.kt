package com.example.echo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.echo.BroadcastContract
import com.example.echo.R
import com.example.echo.navigation.SecondaryActivityToolbar
import androidx.appcompat.widget.Toolbar

class CustomMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_message)
        SecondaryActivityToolbar.configure(
            this,
            findViewById<Toolbar>(R.id.secondary_toolbar),
            getString(R.string.custom_message_title)
        )

        val messageInput = findViewById<EditText>(R.id.message_input)
        findViewById<android.view.View>(R.id.send_message_button).setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isBlank()) {
                messageInput.error = getString(R.string.enter_message_hint)
                messageInput.requestFocus()
                return@setOnClickListener
            }

            startActivity(
                Intent(this, CustomBroadcastActivity::class.java)
                    .putExtra(BroadcastContract.EXTRA_MESSAGE, message)
            )
        }
    }
}
