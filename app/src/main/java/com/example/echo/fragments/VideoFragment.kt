package com.example.echo.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.echo.R

class VideoFragment : Fragment() {
    private var videoView: VideoView? = null
    private var playbackStatus: TextView? = null
    private var mediaController: MediaController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val player = view.findViewById<VideoView>(R.id.video_view)
        val status = view.findViewById<TextView>(R.id.video_status_text)
        videoView = player
        playbackStatus = status

        val controller = MediaController(requireContext())
        controller.setAnchorView(player)
        mediaController = controller
        player.setMediaController(controller)

        // Register listeners before assigning the URI so preparation or
        // playback errors are reflected in the UI instead of leaving a
        // permanently black, unexplained player surface.
        player.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(1.0f, 1.0f)
            status.setText(R.string.video_playing)
            player.start()
            controller.show(5000)
        }

        player.setOnCompletionListener {
            status.setText(R.string.video_completed)
            controller.show(5000)
        }

        player.setOnErrorListener { _, _, _ ->
            status.setText(R.string.video_playback_error)
            true
        }

        player.setVideoURI(
            Uri.parse("android.resource://${requireContext().packageName}/${R.raw.sample_video}")
        )
    }

    override fun onPause() {
        videoView?.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        videoView?.stopPlayback()
        mediaController?.hide()
        mediaController = null
        playbackStatus = null
        videoView = null
        super.onDestroyView()
    }
}
