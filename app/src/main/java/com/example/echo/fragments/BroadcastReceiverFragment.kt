package com.example.echo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.echo.R
import com.example.echo.activities.BatteryActivity
import com.example.echo.activities.CustomMessageActivity

class BroadcastReceiverFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_broadcast_receiver, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinner = view.findViewById<Spinner>(R.id.broadcast_type_spinner)
        val proceedButton = view.findViewById<Button>(R.id.proceed_button)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.broadcast_options,
            R.layout.spinner_selected_item
        ).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
        spinner.adapter = adapter

        proceedButton.setOnClickListener {
            val nextActivity = if (spinner.selectedItemPosition == 0) {
                CustomMessageActivity::class.java
            } else {
                BatteryActivity::class.java
            }
            startActivity(Intent(requireContext(), nextActivity))
        }
    }
}
