package com.example.echo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.echo.fragments.AudioFragment
import com.example.echo.fragments.BroadcastReceiverFragment
import com.example.echo.fragments.ImageScaleFragment
import com.example.echo.fragments.VideoFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_menu_24)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            val destination = when (item.itemId) {
                R.id.nav_broadcast -> Pair(BroadcastReceiverFragment(), R.string.broadcast_receiver)
                R.id.nav_image_scale -> Pair(ImageScaleFragment(), R.string.image_scale)
                R.id.nav_video -> Pair(VideoFragment(), R.string.video)
                R.id.nav_audio -> Pair(AudioFragment(), R.string.audio)
                else -> null
            }

            if (destination != null) {
                showFragment(destination.first, destination.second)
                item.isChecked = true
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            destination != null
        }

        if (savedInstanceState == null) {
            showFragment(BroadcastReceiverFragment(), R.string.broadcast_receiver)
            navigationView.setCheckedItem(R.id.nav_broadcast)
        }
        openDrawerIfRequested()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openDrawerIfRequested()
    }

    private fun openDrawerIfRequested() {
        if (intent.getBooleanExtra(EXTRA_OPEN_DRAWER, false)) {
            intent.removeExtra(EXTRA_OPEN_DRAWER)
            drawerLayout.post {
                if (!isFinishing && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
    }

    private fun showFragment(fragment: Fragment, titleResource: Int) {
        toolbar.title = getString(titleResource)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val EXTRA_OPEN_DRAWER = "com.example.echo.EXTRA_OPEN_DRAWER"
    }
}
