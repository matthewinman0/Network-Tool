package com.matthew.network-tool

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        val button = Button(this)

        textView.text = "Hello Matthew 👋"
        button.text = "Click Me"

        button.setOnClickListener {
            textView.text = "Button Clicked!"
        }

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.addView(textView)
        layout.addView(button)

        setContentView(layout)
    }
}
