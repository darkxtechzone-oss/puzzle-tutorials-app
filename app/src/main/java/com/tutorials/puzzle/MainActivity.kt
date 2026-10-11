package com.tutorials.puzzle

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // BADILISHA HAPA: weka tutorials zako (jina + link)
    private val tutorials = listOf(
        "Tutorial 1: Intro" to "https://www.youtube.com/",
        "Tutorial 2: Basics" to "https://www.youtube.com/",
        "Tutorial 3: Advanced" to "https://www.youtube.com/",
        "Tutorial 4: Projects" to "https://www.youtube.com/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (20 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }
        root.addView(TextView(this).apply {
            text = "🧩 Puzzle Tutorials"
            textSize = 28f
            setTextColor(Color.parseColor("#6C3CE9"))
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "Suluhisha puzzle ili kufungua tutorial!"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, pad / 2, 0, pad)
        })
        tutorials.forEach { (title, url) ->
            root.addView(Button(this).apply {
                text = "🔒  $title"
                textSize = 18f
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, PuzzleActivity::class.java)
                        .putExtra("title", title).putExtra("url", url))
                }
            }, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = pad / 2 })
        }
        setContentView(ScrollView(this).apply { addView(root) })
    }
}
