package com.example.musiclist

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.musiclist.R

class MainActivity  : AppCompatActivity() {
// zz
    private lateinit var albumArtImageView: ImageView
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var seekBar: SeekBar


    private val musicTracks = arrayOf(
        MusicTrack("Track 1", R.raw.pathan, R.drawable.img_5),
        MusicTrack("Track 2", R.raw.sohigh, R.drawable.img_1),
        MusicTrack("Track 3", R.raw.s295, R.drawable.img)
        // Add more music tracks with their corresponding raw resources and album art as needed
    )

    private var currentTrackIndex = 0
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isPaused = false
    private var currentDuration = 0
    private var totalDuration = 0
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        albumArtImageView = findViewById(R.id.albumArtImageView)
        nextButton = findViewById(R.id.nextButton)
        previousButton = findViewById(R.id.previousButton)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        seekBar = findViewById(R.id.seekBar)
       val intent = Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI)
        startActivity(intent)


        // Set initial album art
        updateAlbumArt()

        nextButton.setOnClickListener {
            playNextTrack()
        }

        previousButton.setOnClickListener {
            playPreviousTrack()
        }

        playButton.setOnClickListener {
            if (!isPlaying && !isPaused) {
                playTrack()
            } else {
                resumeTrack()
            }
        }

        pauseButton.setOnClickListener {
            pauseTrack()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentDuration = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer?.seekTo(currentDuration)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateAlbumArt() {
        val currentTrack = musicTracks[currentTrackIndex]
        albumArtImageView.setImageResource(currentTrack.albumArt)
    }

    private fun playTrack() {
        mediaPlayer?.release()
        mediaPlayer = null

        val currentTrack = musicTracks[currentTrackIndex]
        mediaPlayer = MediaPlayer.create(this, currentTrack.rawResource)
        mediaPlayer?.setOnPreparedListener {
            totalDuration = it.duration
            seekBar.max = totalDuration
            mediaPlayer?.start()
            isPlaying = true
            isPaused = false
            updateButtons()
            updateSeekBar()
        }
    }

    private fun resumeTrack() {
        mediaPlayer?.start()
        isPlaying = true
        isPaused = false
        updateButtons()
        updateSeekBar()
    }

    private fun pauseTrack() {
        mediaPlayer?.pause()
        isPlaying = false
        isPaused = true
        updateButtons()
        handler.removeCallbacksAndMessages(null)
    }

    private fun playNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % musicTracks.size
        playTrack()
        updateAlbumArt()
    }

    private fun playPreviousTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + musicTracks.size) % musicTracks.size
        playTrack()
        updateAlbumArt()
    }

    private fun updateButtons() {
        if (isPlaying || isPaused) {
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        } else {
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
        }
    }

    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                currentDuration = mediaPlayer?.currentPosition ?: 0
                seekBar.progress = currentDuration
                handler.postDelayed(this, 1000)
            }
        }, 0)
    }
}

data class MusicTrack(val title: String, val rawResource: Int, val albumArt: Int)