package com.example.echo.fragments

import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.echo.R
import com.google.android.material.button.MaterialButton

/**
 * In-app video player backed by MediaPlayer + TextureView.
 * TextureView is used instead of VideoView/SurfaceView so video frames are
 * composited with the normal view hierarchy on emulators and devices where
 * SurfaceView composition can appear as a black rectangle.
 */
class VideoFragment : Fragment(), TextureView.SurfaceTextureListener {

    private var textureView: TextureView? = null
    private var playbackStatus: TextView? = null
    private var mediaPlayer: MediaPlayer? = null
    private var outputSurface: Surface? = null
    private var isPrepared = false
    private var firstFrameRendered = false
    private var resumeAfterPause = false

    private var playButton: MaterialButton? = null
    private var pauseButton: MaterialButton? = null
    private var replayButton: MaterialButton? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val firstFrameTimeout = Runnable {
        if (isPrepared && !firstFrameRendered) {
            playbackStatus?.setText(R.string.video_no_frame)
            Log.w(TAG, "MediaPlayer prepared and started, but no video-rendering-start event arrived")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textureView = view.findViewById(R.id.video_texture_view)
        playbackStatus = view.findViewById(R.id.video_status_text)
        playButton = view.findViewById(R.id.play_video_button)
        pauseButton = view.findViewById(R.id.pause_video_button)
        replayButton = view.findViewById(R.id.replay_video_button)

        setControlsEnabled(false)
        textureView?.surfaceTextureListener = this

        playButton?.setOnClickListener { startPlayback(restartIfComplete = true) }
        pauseButton?.setOnClickListener {
            mediaPlayer?.let { player ->
                if (isPrepared && player.isPlaying) {
                    player.pause()
                    playbackStatus?.setText(R.string.video_paused)
                }
            }
        }
        replayButton?.setOnClickListener {
            startPlayback(restartIfComplete = true, alwaysRestart = true)
        }

        // TextureView may already have a surface if this view was recreated.
        textureView?.let { texture ->
            if (texture.isAvailable) {
                texture.surfaceTexture?.let(::preparePlayer)
            }
        }
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        preparePlayer(surfaceTexture)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) = Unit

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        releasePlayer()
        return true
    }

    private fun preparePlayer(surfaceTexture: SurfaceTexture) {
        val context = context ?: return
        releasePlayer()

        val surface = Surface(surfaceTexture)
        outputSurface = surface
        val player = MediaPlayer()
        mediaPlayer = player
        firstFrameRendered = false
        isPrepared = false

        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build()
            )
            player.setSurface(surface)
            val videoUri = Uri.parse(
                "android.resource://" + context.packageName + "/" + R.raw.sample_video
            )
            player.setDataSource(context, videoUri)

            player.setOnPreparedListener { preparedPlayer ->
                if (mediaPlayer !== preparedPlayer || view == null) return@setOnPreparedListener

                isPrepared = true
                preparedPlayer.setVolume(1.0f, 1.0f)
                setControlsEnabled(true)
                playbackStatus?.setText(R.string.video_waiting_for_frame)

                // Start automatically, but report successful playback only after
                // Android confirms that the first video frame has been rendered.
                preparedPlayer.start()
                mainHandler.removeCallbacks(firstFrameTimeout)
                mainHandler.postDelayed(firstFrameTimeout, FIRST_FRAME_TIMEOUT_MS)
            }

            player.setOnInfoListener { _, what, _ ->
                when (what) {
                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                        firstFrameRendered = true
                        mainHandler.removeCallbacks(firstFrameTimeout)
                        playbackStatus?.setText(R.string.video_playing)
                        true
                    }

                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        playbackStatus?.setText(R.string.video_buffering)
                        true
                    }

                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        playbackStatus?.setText(
                            if (firstFrameRendered) R.string.video_playing
                            else R.string.video_waiting_for_frame
                        )
                        true
                    }

                    else -> false
                }
            }

            player.setOnCompletionListener {
                mainHandler.removeCallbacks(firstFrameTimeout)
                playbackStatus?.setText(R.string.video_completed)
            }

            player.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "Video playback failed: what=" + what + ", extra=" + extra)
                isPrepared = false
                mainHandler.removeCallbacks(firstFrameTimeout)
                setControlsEnabled(false)
                playbackStatus?.setText(
                    getString(R.string.video_playback_error_details, what, extra)
                )
                true
            }

            playbackStatus?.setText(R.string.video_preparing)
            player.prepareAsync()
        } catch (exception: Exception) {
            Log.e(TAG, "Unable to prepare bundled video", exception)
            playbackStatus?.setText(R.string.video_playback_error)
            setControlsEnabled(false)
            releasePlayer()
        }
    }

    private fun startPlayback(restartIfComplete: Boolean, alwaysRestart: Boolean = false) {
        val player = mediaPlayer ?: return
        if (!isPrepared) {
            playbackStatus?.setText(R.string.video_preparing)
            return
        }

        try {
            if (alwaysRestart || (restartIfComplete && player.currentPosition >= player.duration)) {
                player.seekTo(0)
                firstFrameRendered = false
            }
            if (!player.isPlaying) player.start()
            if (!firstFrameRendered) {
                playbackStatus?.setText(R.string.video_waiting_for_frame)
                mainHandler.removeCallbacks(firstFrameTimeout)
                mainHandler.postDelayed(firstFrameTimeout, FIRST_FRAME_TIMEOUT_MS)
            } else {
                playbackStatus?.setText(R.string.video_playing)
            }
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "Video player entered an invalid state", exception)
            playbackStatus?.setText(R.string.video_playback_error)
        }
    }

    private fun setControlsEnabled(enabled: Boolean) {
        playButton?.isEnabled = enabled
        pauseButton?.isEnabled = enabled
        replayButton?.isEnabled = enabled
    }

    override fun onPause() {
        val player = mediaPlayer
        resumeAfterPause = player != null && isPrepared && player.isPlaying
        if (resumeAfterPause) player?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (resumeAfterPause && isPrepared) {
            resumeAfterPause = false
            startPlayback(restartIfComplete = false)
        }
    }

    private fun releasePlayer() {
        mainHandler.removeCallbacks(firstFrameTimeout)
        isPrepared = false
        firstFrameRendered = false
        resumeAfterPause = false
        try {
            mediaPlayer?.release()
        } catch (exception: Exception) {
            Log.w(TAG, "Error while releasing video player", exception)
        }
        mediaPlayer = null
        outputSurface?.release()
        outputSurface = null
    }

    override fun onDestroyView() {
        textureView?.surfaceTextureListener = null
        releasePlayer()
        textureView = null
        playbackStatus = null
        playButton = null
        pauseButton = null
        replayButton = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "EchoVideoPlayer"
        private const val FIRST_FRAME_TIMEOUT_MS = 5_000L
    }
}
