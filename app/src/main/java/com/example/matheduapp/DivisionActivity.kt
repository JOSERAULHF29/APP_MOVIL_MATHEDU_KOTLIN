package com.example.matheduapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import androidx.compose.ui.unit.sp


class DivisionActivity : AppCompatActivity() {

    private lateinit var tvOperacion: TextView
    private lateinit var etRespuesta: EditText
    private lateinit var btnVerificar: Button
    private lateinit var tvResultado: TextView
    private lateinit var tvPuntuacion: TextView
    private lateinit var konfettiView: KonfettiView
    private var resultadoActual = 0
    private var puntuacion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operacion)

        tvOperacion = findViewById(R.id.tvOperacion)
        etRespuesta = findViewById(R.id.etRespuesta)
        btnVerificar = findViewById(R.id.btnVerificar)
        tvResultado = findViewById(R.id.tvResultado)
        tvPuntuacion = findViewById(R.id.tvPuntuacion)
        konfettiView = findViewById(R.id.konfettiView)

        generarOperacion()
        btnVerificar.setOnClickListener { verificarRespuesta() }
    }

    private fun generarOperacion() {
        val b = Random.nextInt(1, 13)
        val resultado = Random.nextInt(1, 13)
        val a = b * resultado
        resultadoActual = resultado
        tvOperacion.text = "$a ÷ $b = ?"
        etRespuesta.text.clear()
    }

    private fun verificarRespuesta() {
        val respuestaStr = etRespuesta.text.toString()
        val respuesta = respuestaStr.toIntOrNull() ?: return

        val prefs = getSharedPreferences("logros", MODE_PRIVATE)
        val editor = prefs.edit()

        if (respuesta == resultadoActual) {
            tvResultado.text = "✅ Correcto"
            puntuacion++
            tvPuntuacion.text = "Puntuación: $puntuacion"
            editor.putInt("divisionCorrectas", prefs.getInt("divisionCorrectas", 0) + 1)
            lanzarConfeti()
        } else {
            tvResultado.text = "❌ Incorrecto ($resultadoActual)"
        }

        editor.apply()
        etRespuesta.postDelayed({ generarOperacion() }, 1000)
    }

    private fun lanzarConfeti() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xFF9C27B0.toInt(), 0xFF03A9F4.toInt(), 0xFFFFEB3B.toInt()),
            position = Position.Relative(0.5, 0.0),
            emitter = Emitter(duration = 2, TimeUnit.SECONDS).max(100),

        )
        konfettiView.start(party)
    }
}
