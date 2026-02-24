package com.matthew.networktool

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import java.net.InetAddress

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private lateinit var hostInput: TextInputEditText
    private lateinit var pingButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.resultText)
        hostInput = findViewById(R.id.hostInput)
        pingButton = findViewById(R.id.pingButton)

        pingButton.setOnClickListener {
            val host = hostInput.text.toString()
            if (host.isNotEmpty()) {
                pingHost(host)
            }
        }
    }

    private fun pingHost(host: String) {
        resultText.text = "Pinging..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val start = System.currentTimeMillis()
                val address = InetAddress.getByName(host)
                val reachable = address.isReachable(3000)
                val end = System.currentTimeMillis()

                val time = end - start

                withContext(Dispatchers.Main) {
                    if (reachable) {
                        resultText.text =
                            "Host reachable\nIP: ${address.hostAddress}\nTime: ${time}ms"
                    } else {
                        resultText.text = "Host unreachable"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultText.text = "Error: ${e.message}"
                }
            }
        }
    }
}