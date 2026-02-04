package com.example.matheduapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        tvWelcome = findViewById(R.id.tvWelcome)

        // Referencias a los MaterialCardView
        val cardSumas = findViewById<LinearLayout>(R.id.cardSumas)
        val cardRestas = findViewById<LinearLayout>(R.id.cardRestas)
        val cardMultiplicacion = findViewById<LinearLayout>(R.id.cardMultiplicacion)
        val cardDivision = findViewById<LinearLayout>(R.id.cardDivision)

        // Referencia al BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Cargar nombre desde Firebase
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val dbRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId)

            dbRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val nombre = snapshot.child("nombre").value.toString()
                    tvWelcome.text = "Bienvenido, $nombre 🎉"
                } else {
                    tvWelcome.text = "Bienvenido 👋"
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }
        }

        // Eventos de las operaciones
        cardSumas.setOnClickListener {
            startActivity(Intent(this, SumasActivity::class.java))
        }
        cardRestas.setOnClickListener {
            startActivity(Intent(this, RestasActivity::class.java))
        }
        cardMultiplicacion.setOnClickListener {
            startActivity(Intent(this, MultiplicacionActivity::class.java))
        }
        cardDivision.setOnClickListener {
            startActivity(Intent(this, DivisionActivity::class.java))
        }

        // Eventos del BottomNavigationView
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navPerfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }

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

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
