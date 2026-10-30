package com.example.echo.fragments

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.echo.R
import com.google.android.material.button.MaterialButton

class AudioFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private var playbackStatus: TextView? = null
    private var audioPrepared = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_audio, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val status = view.findViewById<TextView>(R.id.audio_status_text)
        playbackStatus = status

        val playButton = view.findViewById<MaterialButton>(R.id.play_audio_button)
        val pauseButton = view.findViewById<MaterialButton>(R.id.pause_audio_button)
        val stopButton = view.findViewById<MaterialButton>(R.id.stop_audio_button)

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val context = requireContext()
        val player = MediaPlayer()
        mediaPlayer = player

        // AudioAttributes must be applied to a MediaPlayer instance before
        // setting its data source and preparing it. There is no
        // MediaPlayer.create(Context, resourceId, AudioAttributes) overload.
        try {
            player.setAudioAttributes(attributes)
            val audioUri = Uri.parse(
                "android.resource://${context.packageName}/${R.raw.sample_audio}"
            )
            player.setDataSource(context, audioUri)

            player.setOnPreparedListener { preparedPlayer ->
                preparedPlayer.setVolume(1.0f, 1.0f)
                audioPrepared = true
                status.setText(R.string.audio_ready)
                playButton.isEnabled = true
                pauseButton.isEnabled = true
                stopButton.isEnabled = true
            }

            player.setOnCompletionListener {
                playbackStatus?.setText(R.string.audio_completed)
            }

            player.setOnErrorListener { _, _, _ ->
                audioPrepared = false
                playbackStatus?.setText(R.string.audio_playback_error)
                playButton.isEnabled = false
                pauseButton.isEnabled = false
                stopButton.isEnabled = false
                true
            }

            status.setText(R.string.audio_preparing)
            playButton.isEnabled = false
            pauseButton.isEnabled = false
            stopButton.isEnabled = false
            player.prepareAsync()
        } catch (_: Exception) {
            player.release()
            mediaPlayer = null
            status.setText(R.string.audio_playback_error)
            playButton.isEnabled = false
            pauseButton.isEnabled = false
            stopButton.isEnabled = false
            return
        }

        playButton.setOnClickListener {
            mediaPlayer?.let { currentPlayer ->
                if (!audioPrepared) {
                    status.setText(R.string.audio_preparing)
                    return@setOnClickListener
                }
                try {
                    if (currentPlayer.currentPosition >= currentPlayer.duration) {
                        currentPlayer.seekTo(0)
                    }
                    if (!currentPlayer.isPlaying) {
                        currentPlayer.start()
                    }
                    status.setText(R.string.audio_playing)
                } catch (_: IllegalStateException) {
                    status.setText(R.string.audio_playback_error)
                }
            }
        }

        pauseButton.setOnClickListener {
            mediaPlayer?.let { currentPlayer ->
                if (audioPrepared && currentPlayer.isPlaying) {
                    currentPlayer.pause()
                    status.setText(R.string.audio_paused)
                }
            }
        }

        stopButton.setOnClickListener {
            mediaPlayer?.let { currentPlayer ->
                if (audioPrepared) {
                    if (currentPlayer.isPlaying) currentPlayer.pause()
                    currentPlayer.seekTo(0)
                    status.setText(R.string.audio_stopped)
                }
            }
        }
    }

    override fun onStop() {
        mediaPlayer?.let {
            if (audioPrepared && it.isPlaying) {
                it.pause()
                playbackStatus?.setText(R.string.audio_paused)
            }
        }
        super.onStop()
    }

    override fun onDestroyView() {
        mediaPlayer?.release()
        mediaPlayer = null
        audioPrepared = false
        playbackStatus = null
        super.onDestroyView()
    }
}
