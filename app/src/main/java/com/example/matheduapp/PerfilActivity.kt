package com.example.matheduapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PerfilActivity : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvEdad: TextView
    private lateinit var spinnerGrado: Spinner
    private lateinit var tvCorreo: TextView
    private lateinit var btnCerrarSesion: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private val grados = arrayOf(
        "1° Primaria", "2° Primaria", "3° Primaria",
        "4° Primaria", "5° Primaria", "6° Primaria"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Referencias UI
        tvNombre = findViewById(R.id.tvNombre)
        tvEdad = findViewById(R.id.tvEdad)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        tvCorreo = findViewById(R.id.tvCorreo)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Spinner de grados
        spinnerGrado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, grados)

        // Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            reference = database.getReference("usuarios").child(userId)
            cargarDatosUsuario()
        }

        btnCerrarSesion.setOnClickListener {
            vibrar()
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Barra inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.navPerfil

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navPerfil -> true
                R.id.navLogros -> {
                    startActivity(Intent(this, LogrosActivity::class.java))
                    finish()
                    true
                }

                R.id.navLogout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarDatosUsuario() {
        val user = auth.currentUser
        tvCorreo.text = "Correo: ${user?.email}"

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvNombre.setText(snapshot.child("nombre").value?.toString() ?: "")
                tvEdad.setText(snapshot.child("edad").value?.toString() ?: "")
                val grado = snapshot.child("grado").value?.toString() ?: ""
                val index = grados.indexOf(grado)
                if (index >= 0) spinnerGrado.setSelection(index)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PerfilActivity, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun vibrar() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        }
    }
}

