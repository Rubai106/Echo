package com.example.echo.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.echo.R
import com.example.echo.ui.ZoomableImageView
import java.net.HttpURLConnection
import java.net.URL

class ImageScaleFragment : Fragment() {
    private var imageDownloadThread: Thread? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_image_scale, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ZoomableImageView>(R.id.zoomable_image)
        val statusText = view.findViewById<TextView>(R.id.image_status)
        loadImage(imageView, statusText)
    }

    private fun loadImage(imageView: ZoomableImageView, statusText: TextView) {
        imageDownloadThread = Thread {
            var connection: HttpURLConnection? = null
            try {
                connection = URL(IMAGE_URL).openConnection() as HttpURLConnection
                connection.connectTimeout = 10_000
                connection.readTimeout = 15_000
                connection.requestMethod = "GET"
                // Some image CDNs refuse requests with no User-Agent; send
                // an identifying one so the download is not rejected.
                connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Android) Echo"
                )
                connection.setRequestProperty("Accept", "image/*")
                connection.connect()
                val bitmap = connection.inputStream.use { input -> BitmapFactory.decodeStream(input) }
                activity?.runOnUiThread {
                    if (isAdded && view != null) {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                            imageView.fitToView()
                            statusText.visibility = View.GONE
                        } else {
                            statusText.setText(R.string.image_load_failed)
                        }
                    }
                }
            } catch (_: Exception) {
                activity?.runOnUiThread {
                    if (isAdded && view != null) {
                        statusText.setText(R.string.image_load_failed)
                    }
                }
            } finally {
                connection?.disconnect()
            }
        }.apply {
            name = "assignment-image-download"
            start()
        }
    }

    override fun onDestroyView() {
        imageDownloadThread = null
        super.onDestroyView()
    }

    companion object {
        private const val IMAGE_URL =
            "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?fit=crop&w=1400&q=80&fm=jpg"
    }
}
