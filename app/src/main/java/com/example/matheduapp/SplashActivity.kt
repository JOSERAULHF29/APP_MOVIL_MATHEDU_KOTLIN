package com.example.matheduapp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppName: TextView
    private lateinit var tvSlogan: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        imgLogo = findViewById(R.id.imgLogo)
        tvAppName = findViewById(R.id.tvAppName)
        tvSlogan = findViewById(R.id.tvSlogan)
        progressBar = findViewById(R.id.progressBar)

        // 🎵 Sonido de bienvenida
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_sound)
        mediaPlayer.start()

        // ✨ Animaciones
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        imgLogo.startAnimation(fadeIn)
        tvAppName.startAnimation(slideUp)
        tvSlogan.startAnimation(slideUp)

        // ⏳ Espera 3.5 segundos y redirige
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            val nextActivity = if (currentUser != null) {
                // Ya hay sesión iniciada
                MainActivity::class.java
            } else {
                // No hay sesión → ir al login
                LoginActivity::class.java
            }

            val intent = Intent(this, nextActivity)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3500)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}


