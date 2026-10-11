package com.tutorials.puzzle

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

/** Sliding puzzle 3x3: panga namba 1-8 kwa mpangilio, kisha tutorial inafunguka. */
class PuzzleActivity : AppCompatActivity() {

    private val tiles = IntArray(9) { (it + 1) % 9 }   // 1..8, 0 = nafasi tupu
    private val buttons = ArrayList<Button>()
    private lateinit var movesText: TextView
    private var moves = 0
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = intent.getStringExtra("url") ?: ""
        val title = intent.getStringExtra("title") ?: "Tutorial"
        val d = resources.displayMetrics.density
        val pad = (16 * d).toInt()
        val size = (resources.displayMetrics.widthPixels - pad * 2) / 3

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(pad, pad * 2, pad, pad)
        }
        root.addView(TextView(this).apply { text = title; textSize = 22f; gravity = Gravity.CENTER })
        movesText = TextView(this).apply { textSize = 16f; gravity = Gravity.CENTER; setPadding(0, pad, 0, pad) }
        root.addView(movesText)

        for (r in 0 until 3) {
            val row = LinearLayout(this)
            for (c in 0 until 3) {
                val i = r * 3 + c
                val b = Button(this).apply {
                    textSize = 32f
                    setTextColor(Color.WHITE)
                    setOnClickListener { onTile(i) }
                }
                buttons.add(b)
                row.addView(b, LinearLayout.LayoutParams(size, size))
            }
            root.addView(row)
        }
        root.addView(Button(this).apply {
            text = "Changanya upya"
            setOnClickListener { shuffle() }
        })
        setContentView(root)
        shuffle()
    }

    private fun shuffle() {
        // Tunatembeza tile kutoka hali iliyosuluhishwa => puzzle inasuluhishika kila mara
        var empty = 8
        var prev = -1
        repeat(200) {
            val r = empty / 3; val c = empty % 3
            val n = mutableListOf<Int>()
            if (r > 0) n.add(empty - 3)
            if (r < 2) n.add(empty + 3)
            if (c > 0) n.add(empty - 1)
            if (c < 2) n.add(empty + 1)
            n.remove(prev)
            val pick = n[Random.nextInt(n.size)]
            tiles[empty] = tiles[pick]; tiles[pick] = 0
            prev = empty; empty = pick
        }
        moves = 0
        render()
    }

    private fun onTile(i: Int) {
        val e = tiles.indexOf(0)
        val adjacent = (i / 3 == e / 3 && Math.abs(i - e) == 1) || (i % 3 == e % 3 && Math.abs(i - e) == 3)
        if (!adjacent) return
        tiles[e] = tiles[i]; tiles[i] = 0
        moves++
        render()
        if (tiles.indices.all { tiles[it] == (it + 1) % 9 }) win()
    }

    private fun render() {
        movesText.text = "Hatua: $moves"
        buttons.forEachIndexed { i, b ->
            val v = tiles[i]
            b.text = if (v == 0) "" else v.toString()
            b.setBackgroundColor(if (v == 0) Color.TRANSPARENT else Color.parseColor("#6C3CE9"))
        }
    }

    private fun win() {
        AlertDialog.Builder(this)
            .setTitle("🎉 Hongera!")
            .setMessage("Umesuluhisha kwa hatua $moves. Tutorial imefunguka!")
            .setCancelable(false)
            .setPositiveButton("Fungua Tutorial") { _, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                finish()
            }
            .setNegativeButton("Rudi") { _, _ -> finish() }
            .show()
    }
}
