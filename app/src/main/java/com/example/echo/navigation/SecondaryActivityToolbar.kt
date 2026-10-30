package com.example.echo.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.echo.R

/** Shared up-navigation for activities opened from the main screen. */
object SecondaryActivityToolbar {
    fun configure(activity: AppCompatActivity, toolbar: Toolbar, title: String) {
        toolbar.title = title
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
        toolbar.setNavigationContentDescription(R.string.navigate_back)
        toolbar.setNavigationOnClickListener {
            activity.finish()
        }
    }
}
