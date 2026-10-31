package com.tobirama.sensi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(15, 10, 25))
        }
        val title = TextView(this).apply {
            text = "TOBIRAMA SENSI\nPURPLE EDITION"
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(220, 190, 255))
        }
        val info = TextView(this).apply {
            text = "Ative a permissão de sobreposição para mostrar a bolinha flutuante.\n\nOs controles são apenas visuais e não alteram o Free Fire."
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 24, 0, 24)
        }
        val start = Button(this).apply {
            text = "ATIVAR BOLINHA FLUTUANTE"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                    startActivity(intent)
                } else {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                    finish()
                }
            }
        }
        layout.addView(title)
        layout.addView(info)
        layout.addView(start)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        // Após conceder a permissão, o usuário pode tocar no botão para iniciar a bolinha.
    }
}
