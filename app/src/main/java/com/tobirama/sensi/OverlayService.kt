package com.tobirama.sensi

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var panel: LinearLayout? = null
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var panelParams: WindowManager.LayoutParams

    private fun bg(color: Int, stroke: Int = Color.TRANSPARENT): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = 34f
            if (stroke != Color.TRANSPARENT) setStroke(2, stroke)
        }

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val overlayType = if (android.os.Build.VERSION.SDK_INT >= 26)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

        bubbleParams = WindowManager.LayoutParams(
            72, 72, overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 40; y = 260 }

        val b = TextView(this).apply {
            text = "千"
            textSize = 27f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = bg(Color.rgb(54, 25, 83), Color.rgb(197, 151, 255))
            elevation = 16f
        }
        bubble = b
        b.setOnTouchListener(object : View.OnTouchListener {
            var downX = 0f; var downY = 0f; var startX = 0; var startY = 0
            var dragged = false
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = e.rawX; downY = e.rawY
                        startX = bubbleParams.x; startY = bubbleParams.y
                        dragged = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (e.rawX - downX).toInt()
                        val dy = (e.rawY - downY).toInt()
                        if (kotlin.math.abs(dx) + kotlin.math.abs(dy) > 8) dragged = true
                        bubbleParams.x = startX + dx; bubbleParams.y = startY + dy
                        wm.updateViewLayout(b, bubbleParams)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!dragged) togglePanel()
                        return true
                    }
                }
                return true
            }
        })
        wm.addView(b, bubbleParams)
    }

    private fun togglePanel() {
        if (panel != null) {
            try { wm.removeView(panel) } catch (_: Exception) {}
            panel = null
            return
        }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 18, 22, 18)
            background = bg(Color.rgb(25, 17, 38), Color.rgb(151, 92, 220))
            elevation = 18f
        }
        val header = TextView(this).apply {
            text = "TOBIRAMA SENSI  ✕"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(218, 187, 255))
            setPadding(0, 0, 0, 14)
            setOnClickListener { togglePanel() }
        }
        box.addView(header)
        listOf("Calibrar mira", "HS Capa", "Mira sem tremer", "Modo leve").forEach { label ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(10, 8, 4, 8)
                background = bg(Color.rgb(43, 32, 58))
            }
            val name = TextView(this).apply {
                text = label
                textSize = 14f
                setTextColor(Color.WHITE)
            }
            val toggle = android.widget.Switch(this).apply {
                text = ""
                buttonTintList = android.content.res.ColorStateList.valueOf(Color.rgb(178, 115, 255))
                setOnCheckedChangeListener { _, _ -> }
            }
            row.addView(name, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(toggle)
            val lp = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 8 }
            box.addView(row, lp)
        }
        val foot = TextView(this).apply {
            text = "Demonstração visual. Não modifica o jogo."
            textSize = 10f
            setTextColor(Color.LTGRAY)
            setPadding(2, 8, 2, 0)
        }
        box.addView(foot)
        panelParams = WindowManager.LayoutParams(
            300, WindowManager.LayoutParams.WRAP_CONTENT, 
            if (android.os.Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (bubbleParams.x - 210).coerceAtLeast(8)
            y = (bubbleParams.y + 82).coerceAtMost(850)
        }
        panel = box
        wm.addView(box, panelParams)
    }

    override fun onDestroy() {
        try { bubble?.let { wm.removeView(it) } } catch (_: Exception) {}
        try { panel?.let { wm.removeView(it) } } catch (_: Exception) {}
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
