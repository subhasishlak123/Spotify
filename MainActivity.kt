package com.example.spotifyclone

import android.content.ComponentName
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

class MainActivity : AppCompatActivity() {
    private var controller: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 120, 60, 60)
        }

        val title = TextView(this).apply {
            text = "Spotify Player"
            textSize = 22f
            setPadding(0, 0, 0, 40)
        }

        val playBtn = Button(this).apply {
            text = "▶ Play Music (Runs in Background)"
            setOnClickListener {
                controller?.let { player ->
                    if (player.mediaItemCount == 0) {
                        val item = MediaItem.fromUri("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
                        player.setMediaItem(item)
                        player.prepare()
                    }
                    player.play()
                }
            }
        }

        val pauseBtn = Button(this).apply {
            text = "⏸ Pause (Stops Background Service)"
            setOnClickListener {
                controller?.pause()
            }
        }

        layout.addView(title)
        layout.addView(playBtn)
        layout.addView(pauseBtn)
        setContentView(layout)
    }

    override fun onStart() {
        super.onStart()
        val token = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, token).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onStop() {
        super.onStop()
        controller?.release()
        controller = null
    }
}
