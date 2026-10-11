package com.tutorials.puzzle

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

/** Sliding puzzle (3x3 au 4x4) yenye muda, hatua, rekodi bora na progress. */
class PuzzleActivity : AppCompatActivity() {

    private var n = 3
    private var tiles = IntArray(9)
    private val cells = ArrayList<TextView>()
    private lateinit var board: LinearLayout
    private lateinit var movesText: TextView
    private lateinit var timeText: TextView
    private lateinit var bestText: TextView
    private lateinit var progress: ProgressBar
    private lateinit var chips: List<TextView>

    private val handler = Handler(Looper.getMainLooper())
    private var startMs = 0L
    private var moves = 0
    private var title = ""
    private var url = ""
    private var boardPx = 0

    private val ticker = object : Runnable {
        override fun run() {
            if (startMs > 0) timeText.text = "⏱ " + fmt(elapsed())
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = this
        title = intent.getStringExtra("title") ?: "Tutorial"
        url = intent.getStringExtra("url") ?: ""
        val pad = Ui.dp(ctx, 20)
        boardPx = resources.displayMetrics.widthPixels - pad * 2

        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, Ui.dp(ctx, 40), pad, pad)
        }
        root.addView(TextView(ctx).apply {
            text = title
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
        })
        root.addView(TextView(ctx).apply {
            text = "Panga namba kwa mpangilio, nafasi tupu iwe mwisho."
            textSize = 14f
            setTextColor(Color.parseColor(Ui.MUTED))
            setPadding(0, Ui.dp(ctx, 4), 0, Ui.dp(ctx, 12))
        })

        // Chagua ukubwa
        val chipRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        chips = listOf(3, 4).map { s ->
            TextView(ctx).apply {
                text = "$s × $s"
                textSize = 15f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(Ui.dp(ctx, 18), Ui.dp(ctx, 8), Ui.dp(ctx, 18), Ui.dp(ctx, 8))
                setOnClickListener { if (n != s) setSize(s) }
            }.also {
                chipRow.addView(it, LinearLayout.LayoutParams(-2, -2).apply { rightMargin = Ui.dp(ctx, 8) })
            }
        }
        root.addView(chipRow)

        // Takwimu
        movesText = TextView(ctx)
        timeText = TextView(ctx)
        val stats = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, Ui.dp(ctx, 12), 0, Ui.dp(ctx, 12))
            background = Ui.rounded(ctx, Color.parseColor(Ui.CARD), 14)
        }
        listOf(movesText, timeText).forEach {
            it.textSize = 16f
            it.setTextColor(Color.WHITE)
            it.gravity = Gravity.CENTER
            stats.addView(it, LinearLayout.LayoutParams(0, -2, 1f))
        }
        root.addView(stats, LinearLayout.LayoutParams(-1, -2).apply { topMargin = Ui.dp(ctx, 12) })

        bestText = TextView(ctx).apply {
            textSize = 14f
            setTextColor(Color.parseColor(Ui.ORANGE))
            gravity = Gravity.CENTER
            setPadding(0, Ui.dp(ctx, 10), 0, Ui.dp(ctx, 6))
        }
        root.addView(bestText)

        progress = ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply {
            progressTintList = ColorStateList.valueOf(Color.parseColor(Ui.ORANGE))
            progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor(Ui.CARD2))
        }
        root.addView(progress, LinearLayout.LayoutParams(-1, Ui.dp(ctx, 8)).apply {
            bottomMargin = Ui.dp(ctx, 12)
        })

        board = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(Ui.dp(ctx, 8), Ui.dp(ctx, 8), Ui.dp(ctx, 8), Ui.dp(ctx, 8))
            background = Ui.rounded(ctx, Color.parseColor(Ui.CARD), 20)
        }
        root.addView(board, LinearLayout.LayoutParams(boardPx, -2))

        val btnRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        fun pill(label: String, color: String, action: () -> Unit) = TextView(ctx).apply {
            text = label
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, Ui.dp(ctx, 14), 0, Ui.dp(ctx, 14))
            background = Ui.rounded(ctx, Color.parseColor(color), 14)
            setOnClickListener { action() }
        }
        btnRow.addView(pill("🔀 Changanya", Ui.PURPLE) { shuffle() },
            LinearLayout.LayoutParams(0, -2, 1f).apply { rightMargin = Ui.dp(ctx, 8) })
        btnRow.addView(pill("← Rudi", Ui.CARD2) { finish() }, LinearLayout.LayoutParams(0, -2, 1f))
        root.addView(btnRow, LinearLayout.LayoutParams(-1, -2).apply { topMargin = Ui.dp(ctx, 16) })

        setContentView(ScrollView(ctx).apply { addView(root) })

        tiles = IntArray(n * n)
        buildBoard()
        styleChips()
        shuffle()
        handler.post(ticker)
    }

    private fun setSize(s: Int) {
        n = s
        tiles = IntArray(n * n)
        buildBoard()
        styleChips()
        shuffle()
    }

    private fun styleChips() {
        chips.forEachIndexed { idx, c ->
            val selected = (idx == 0 && n == 3) || (idx == 1 && n == 4)
            c.background = Ui.rounded(this, Color.parseColor(if (selected) Ui.PURPLE else Ui.CARD), 20)
        }
    }

    private fun buildBoard() {
        board.removeAllViews()
        cells.clear()
        val m = Ui.dp(this, 3)
        val size = (boardPx - Ui.dp(this, 16)) / n - 2 * m
        for (r in 0 until n) {
            val row = LinearLayout(this)
            for (c in 0 until n) {
                val i = r * n + c
                val t = TextView(this).apply {
                    gravity = Gravity.CENTER
                    setTextColor(Color.WHITE)
                    setTypeface(null, Typeface.BOLD)
                    setOnClickListener { onTile(i) }
                }
                cells.add(t)
                row.addView(t, LinearLayout.LayoutParams(size, size).apply { setMargins(m, m, m, m) })
            }
            board.addView(row)
        }
    }

    private fun isSolved() = tiles.indices.all { tiles[it] == (it + 1) % (n * n) }

    private fun shuffle() {
        val total = n * n
        do {
            for (i in 0 until total) tiles[i] = (i + 1) % total
            var empty = total - 1
            var prev = -1
            repeat(total * 40) {
                val r = empty / n
                val c = empty % n
                val nb = ArrayList<Int>()
                if (r > 0) nb.add(empty - n)
                if (r < n - 1) nb.add(empty + n)
                if (c > 0) nb.add(empty - 1)
                if (c < n - 1) nb.add(empty + 1)
                if (nb.size > 1) nb.remove(prev)
                val pick = nb[Random.nextInt(nb.size)]
                tiles[empty] = tiles[pick]
                tiles[pick] = 0
                prev = empty
                empty = pick
            }
        } while (isSolved())
        moves = 0
        startMs = 0
        timeText.text = "⏱ 0:00"
        showBest()
        render()
    }

    private fun onTile(i: Int) {
        val e = tiles.indexOf(0)
        val adjacent = (i / n == e / n && Math.abs(i - e) == 1) || (i % n == e % n && Math.abs(i - e) == n)
        if (!adjacent) return
        if (startMs == 0L) startMs = System.currentTimeMillis()
        tiles[e] = tiles[i]
        tiles[i] = 0
        moves++
        render()
        cells[e].scaleX = 0.85f
        cells[e].scaleY = 0.85f
        cells[e].animate().scaleX(1f).scaleY(1f).setDuration(120).start()
        if (isSolved()) win()
    }

    private fun render() {
        val total = n * n
        movesText.text = "👣 Hatua: $moves"
        var correct = 0
        cells.forEachIndexed { i, t ->
            val v = tiles[i]
            t.textSize = if (n == 3) 32f else 24f
            if (v == 0) {
                t.text = ""
                t.background = Ui.rounded(this, Color.parseColor(Ui.CARD2), 14)
            } else {
                t.text = v.toString()
                val hue = (v - 1) * 300f / (total - 1)
                val c1 = Color.HSVToColor(floatArrayOf(hue, 0.55f, 0.95f))
                val c2 = Color.HSVToColor(floatArrayOf(hue, 0.85f, 0.65f))
                t.background = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(c1, c2)).apply {
                    cornerRadius = Ui.dp(this@PuzzleActivity, 14).toFloat()
                }
                if (v == (i + 1) % total) correct++
            }
        }
        progress.max = total - 1
        progress.progress = correct
    }

    private fun elapsed(): Int = if (startMs == 0L) 0 else ((System.currentTimeMillis() - startMs) / 1000).toInt()

    private fun fmt(s: Int) = "%d:%02d".format(s / 60, s % 60)

    private val prefs get() = getSharedPreferences("puzzle", MODE_PRIVATE)

    private fun showBest() {
        val m = prefs.getInt("m_${title}_$n", 0)
        val t = prefs.getInt("t_${title}_$n", 0)
        bestText.text = if (m > 0) "🏆 Bora: hatua $m • ${fmt(t)}" else "🏆 Bora: bado hujacheza"
    }

    private fun win() {
        val secs = elapsed()
        startMs = 0
        val old = prefs.getInt("m_${title}_$n", 0)
        if (old == 0 || moves < old) {
            prefs.edit().putInt("m_${title}_$n", moves).putInt("t_${title}_$n", secs).apply()
        }
        showBest()
        AlertDialog.Builder(this)
            .setTitle("🎉 Hongera!")
            .setMessage("Umemaliza kwa hatua $moves ndani ya ${fmt(secs)}.\nTutorial imefunguka!")
            .setCancelable(false)
            .setPositiveButton("Fungua Tutorial") { _, _ ->
                startActivity(
                    Intent(this, WebActivity::class.java)
                        .putExtra("title", title).putExtra("url", url)
                )
                finish()
            }
            .setNegativeButton("Cheza tena") { _, _ -> shuffle() }
            .show()
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        super.onDestroy()
    }
}
