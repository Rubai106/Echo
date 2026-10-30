package com.example.echo.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        val options = resources.getStringArray(R.array.broadcast_options).toList()
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            options
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View = createOptionView(options[position], isDropdown = false)

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View = createOptionView(options[position], isDropdown = true)
        }

        spinner.adapter = adapter
        spinner.setPopupBackgroundDrawable(ColorDrawable(
            ContextCompat.getColor(requireContext(), R.color.white)
        ))
        spinner.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.primary)
        )
        spinner.dropDownWidth = ViewGroup.LayoutParams.MATCH_PARENT

        proceedButton.setOnClickListener {
            val nextActivity = if (spinner.selectedItemPosition == 0) {
                CustomMessageActivity::class.java
            } else {
                BatteryActivity::class.java
            }
            startActivity(Intent(requireContext(), nextActivity))
        }
    }

    private fun createOptionView(text: String, isDropdown: Boolean): TextView {
        val context = requireContext()
        val density = resources.displayMetrics.density
        fun dp(value: Int): Int = (value * density).toInt()

        return TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                if (isDropdown) ViewGroup.LayoutParams.WRAP_CONTENT else dp(64)
            )
            gravity = Gravity.CENTER_VERTICAL or Gravity.START
            setPadding(dp(16), dp(if (isDropdown) 12 else 8), dp(44), dp(if (isDropdown) 12 else 8))
            minHeight = dp(56)
            maxLines = 2
            ellipsize = TextUtils.TruncateAt.END
            setLineSpacing(dp(2).toFloat(), 1f)
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            setTextSize(
                android.util.TypedValue.COMPLEX_UNIT_SP,
                if (isDropdown) 15f else 16f
            )
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (isDropdown) R.color.white else R.color.screen_background
                )
            )
            this.text = text
        }
    }
}
