package com.example.matheduapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spinnerGrado: Spinner
    private lateinit var etEdad: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        etEdad = findViewById(R.id.etEdad)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Configurar Spinner de grado
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.grados_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapter

        // Acción del botón Crear cuenta
        btnRegister.setOnClickListener {
            registrarUsuario()
        }

        // Enlace al login
        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    private fun registrarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val grado = spinnerGrado.selectedItem.toString()
        val edad = etEdad.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (nombre.isEmpty() || edad.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show()
            return
        }

        val edadNum = edad.toIntOrNull()
        if (edadNum == null || edadNum <= 0) {
            Toast.makeText(this, "Ingrese una edad válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        btnRegister.isEnabled = false // Desactivar mientras se procesa

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                btnRegister.isEnabled = true

                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val userMap = mapOf(
                        "nombre" to nombre,
                        "grado" to grado,
                        "edad" to edad,
                        "email" to email
                    )

                    database.getReference("usuarios").child(userId).setValue(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Cuenta creada correctamente 🎉", Toast.LENGTH_SHORT).show()

                            // Opción 1: Ir directo al Login (como tú tienes)
                            startActivity(Intent(this, LoginActivity::class.java))
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            finish()

                            // Opción 2 (alternativa profesional): ir al MainActivity directamente
                            // val intent = Intent(this, MainActivity::class.java)
                            // startActivity(intent)
                            // finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error al crear cuenta: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}

