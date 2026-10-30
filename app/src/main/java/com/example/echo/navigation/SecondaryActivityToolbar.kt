package com.example.echo.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.echo.MainActivity
import com.example.echo.R

/** Shared, predictable navigation for activities opened from the main drawer flow. */
object SecondaryActivityToolbar {
    fun configure(activity: AppCompatActivity, toolbar: Toolbar, title: String) {
        toolbar.title = title
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
        toolbar.setNavigationContentDescription(R.string.navigate_back)
        toolbar.setNavigationOnClickListener {
            // finish() follows the same back stack as Android's system Back action.
            activity.finish()
        }

        toolbar.inflateMenu(R.menu.secondary_toolbar_menu)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_open_drawer) {
                val intent = Intent(activity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra(MainActivity.EXTRA_OPEN_DRAWER, true)
                }
                activity.startActivity(intent)
                activity.finish()
                true
            } else {
                false
            }
        }
    }
}
