package com.tutorials.puzzle

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = this
        val pad = Ui.dp(ctx, 20)
        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, Ui.dp(ctx, 48), pad, pad)
        }
        root.addView(TextView(ctx).apply {
            text = "🧩 Puzzle Tutorials"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })
        root.addView(TextView(ctx).apply {
            text = "Suluhisha puzzle ili kufungua tutorial!"
            textSize = 15f
            setTextColor(Color.parseColor(Ui.MUTED))
            gravity = Gravity.CENTER
            setPadding(0, Ui.dp(ctx, 8), 0, Ui.dp(ctx, 24))
        })

        // Tutorials zinasomwa kutoka res/values/tutorials.xml
        resources.getStringArray(R.array.tutorials).forEachIndexed { i, raw ->
            val parts = raw.split("|", limit = 2)
            val title = parts[0].trim()
            val url = parts.getOrElse(1) { "" }.trim()

            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(Ui.dp(ctx, 16), Ui.dp(ctx, 16), Ui.dp(ctx, 16), Ui.dp(ctx, 16))
                background = Ui.rounded(ctx, Color.parseColor(Ui.CARD), 16)
                setOnClickListener {
                    startActivity(
                        Intent(ctx, PuzzleActivity::class.java)
                            .putExtra("title", title).putExtra("url", url)
                    )
                }
            }
            val size = Ui.dp(ctx, 40)
            card.addView(TextView(ctx).apply {
                text = "${i + 1}"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                background = Ui.rounded(ctx, Color.parseColor(Ui.PURPLE), 20)
            }, LinearLayout.LayoutParams(size, size))
            card.addView(TextView(ctx).apply {
                text = title
                textSize = 17f
                setTextColor(Color.WHITE)
                setPadding(Ui.dp(ctx, 14), 0, Ui.dp(ctx, 8), 0)
            }, LinearLayout.LayoutParams(0, -2, 1f))
            card.addView(TextView(ctx).apply { text = "🔒"; textSize = 20f })

            root.addView(card, LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = Ui.dp(ctx, 12)
            })
        }
        setContentView(ScrollView(ctx).apply { addView(root) })
    }
}
