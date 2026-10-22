
package com.example.simpletimer

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType

class MainActivity : Activity() {

    private var timer: CountDownTimer? = null
    private var remaining = 300000L
    private var running = false

    private lateinit var display: TextView
    private lateinit var status: TextView
    private lateinit var minutes: EditText
    private lateinit var seconds: EditText
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(247, 248, 252)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 28, 28, 28)
            setBackgroundColor(Color.rgb(247, 248, 252))
        }

        fun label(text: String, size: Float, color: Int) =
            TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
            }

        root.addView(label("Simple Timer", 28f, Color.DKGRAY))

        status = label("Ready when you are", 15f, Color.GRAY)
        root.addView(status)

        display = label("05:00", 56f, Color.rgb(111, 69, 221))
        root.addView(display)

        val inputs = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 12)
        }

        minutes = EditText(this).apply {
            hint = "Minutes"
            setText("5")
            gravity = Gravity.CENTER
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        seconds = EditText(this).apply {
            hint = "Seconds"
            setText("0")
            gravity = Gravity.CENTER
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        inputs.addView(minutes, LinearLayout.LayoutParams(0, 60, 1f))
        inputs.addView(seconds, LinearLayout.LayoutParams(0, 60, 1f))
        root.addView(inputs)

        fun button(text: String): Button {
            return Button(this).apply {
                this.text = text
                isAllCaps = false
            }
        }

        val setButton = button("Set time")
        setButton.setOnClickListener {
            if (running) pauseTimer()

            val m = minutes.text.toString().toLongOrNull() ?: 0L
            val s = seconds.text.toString().toLongOrNull() ?: 0L

            if (m > 999 || s > 59) {
                Toast.makeText(
                    this, "Max: 999 minutes, 59 seconds",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                remaining = (m * 60 + s) * 1000
                updateDisplay()
                status.text = "Ready when you are"
            }
        }
        root.addView(setButton)

        startButton = button("Start")
        startButton.setOnClickListener {
            if (running) pauseTimer() else startTimer()
        }
        root.addView(startButton)

        val resetButton = button("Reset")
        resetButton.setOnClickListener {
            timer?.cancel()
            running = false
            remaining = 300000L
            minutes.setText("5")
            seconds.setText("0")
            startButton.text = "Start"
            status.text = "Ready when you are"
            updateDisplay()
        }
        root.addView(resetButton)

        setContentView(root)
    }

    private fun startTimer() {
        if (remaining <= 0) {
            Toast.makeText(this, "Set a time first", Toast.LENGTH_SHORT).show()
            return
        }

        running = true
        startButton.text = "Pause"
        status.text = "Timer running"

        timer = object : CountDownTimer(remaining, 250) {
            override fun onTick(ms: Long) {
                remaining = ms
                updateDisplay()
            }

            override fun onFinish() {
                remaining = 0
                running = false
                updateDisplay()
                startButton.text = "Start"
                status.text = "Time's up!"
                Toast.makeText(
                    this@MainActivity,
                    "Time's up!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        running = false
        startButton.text = "Resume"
        status.text = "Paused"
    }

    private fun updateDisplay() {
        val total = (remaining + 999) / 1000
        display.text = String.format(
            "%02d:%02d", total / 60, total % 60
        )
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
