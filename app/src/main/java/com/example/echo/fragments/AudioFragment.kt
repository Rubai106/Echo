package com.example.echo.fragments

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private var audioManager: AudioManager? = null
    private var playButton: MaterialButton? = null
    private var pauseButton: MaterialButton? = null
    private var stopButton: MaterialButton? = null
    private var audioPrepared = false

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> mediaPlayer?.setVolume(1.0f, 1.0f)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaPlayer?.setVolume(0.25f, 0.25f)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer?.let { player ->
                    if (audioPrepared && player.isPlaying) player.pause()
                }
                playbackStatus?.setText(R.string.audio_paused)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_audio, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playbackStatus = view.findViewById(R.id.audio_status_text)
        playButton = view.findViewById(R.id.play_audio_button)
        pauseButton = view.findViewById(R.id.pause_audio_button)
        stopButton = view.findViewById(R.id.stop_audio_button)

        val context = requireContext()
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val player = MediaPlayer()
        mediaPlayer = player
        setControlsEnabled(false)

        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            player.setVolume(1.0f, 1.0f)
            val audioUri = Uri.parse(
                "android.resource://" + context.packageName + "/" + R.raw.sample_audio
            )
            player.setDataSource(context, audioUri)

            player.setOnPreparedListener { preparedPlayer ->
                if (mediaPlayer !== preparedPlayer || view == null) return@setOnPreparedListener
                audioPrepared = true
                preparedPlayer.setVolume(1.0f, 1.0f)
                playbackStatus?.setText(R.string.audio_ready)
                setControlsEnabled(true)
            }

            player.setOnCompletionListener {
                playbackStatus?.setText(R.string.audio_completed)
                abandonAudioFocus()
            }

            player.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "Audio playback failed: what=" + what + ", extra=" + extra)
                audioPrepared = false
                playbackStatus?.setText(
                    getString(R.string.audio_playback_error_details, what, extra)
                )
                setControlsEnabled(false)
                abandonAudioFocus()
                true
            }

            playbackStatus?.setText(R.string.audio_preparing)
            player.prepareAsync()
        } catch (exception: Exception) {
            Log.e(TAG, "Unable to prepare bundled audio", exception)
            player.release()
            mediaPlayer = null
            playbackStatus?.setText(R.string.audio_playback_error)
            setControlsEnabled(false)
            return
        }

        playButton?.setOnClickListener {
            val currentPlayer = mediaPlayer ?: return@setOnClickListener
            if (!audioPrepared) {
                playbackStatus?.setText(R.string.audio_preparing)
                return@setOnClickListener
            }

            if (!requestAudioFocus()) {
                playbackStatus?.setText(R.string.audio_focus_denied)
                return@setOnClickListener
            }

            try {
                if (currentPlayer.currentPosition >= currentPlayer.duration) {
                    currentPlayer.seekTo(0)
                }
                if (!currentPlayer.isPlaying) currentPlayer.start()
                playbackStatus?.setText(R.string.audio_playing)
            } catch (exception: IllegalStateException) {
                Log.e(TAG, "Audio player entered an invalid state", exception)
                playbackStatus?.setText(R.string.audio_playback_error)
                abandonAudioFocus()
            }
        }

        pauseButton?.setOnClickListener {
            mediaPlayer?.let { currentPlayer ->
                if (audioPrepared && currentPlayer.isPlaying) {
                    currentPlayer.pause()
                    playbackStatus?.setText(R.string.audio_paused)
                    abandonAudioFocus()
                }
            }
        }

        stopButton?.setOnClickListener {
            mediaPlayer?.let { currentPlayer ->
                if (audioPrepared) {
                    if (currentPlayer.isPlaying) currentPlayer.pause()
                    currentPlayer.seekTo(0)
                    playbackStatus?.setText(R.string.audio_stopped)
                    abandonAudioFocus()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun requestAudioFocus(): Boolean {
        val manager = audioManager ?: return false
        return manager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    @Suppress("DEPRECATION")
    private fun abandonAudioFocus() {
        audioManager?.abandonAudioFocus(audioFocusChangeListener)
    }

    private fun setControlsEnabled(enabled: Boolean) {
        playButton?.isEnabled = enabled
        pauseButton?.isEnabled = enabled
        stopButton?.isEnabled = enabled
    }

    override fun onStop() {
        mediaPlayer?.let { player ->
            if (audioPrepared && player.isPlaying) {
                player.pause()
                playbackStatus?.setText(R.string.audio_paused)
            }
        }
        abandonAudioFocus()
        super.onStop()
    }

    override fun onDestroyView() {
        abandonAudioFocus()
        mediaPlayer?.release()
        mediaPlayer = null
        audioPrepared = false
        audioManager = null
        playbackStatus = null
        playButton = null
        pauseButton = null
        stopButton = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "EchoAudioPlayer"
    }
}
