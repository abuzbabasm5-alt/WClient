package com.retrivedmods.wclient.game.module.visual

import android.annotation.SuppressLint
import android.graphics.*
import com.retrivedmods.wclient.game.Module
import com.retrivedmods.wclient.game.ModuleCategory
import com.retrivedmods.wclient.game.entity.LocalPlayer
import com.retrivedmods.wclient.game.entity.Player

class ESPModule : Module("esp", ModuleCategory.Visual) {

    private val colorRed by intValue("color_red", 255, 0..255)
    private val colorGreen by intValue("color_green", 0, 0..255)
    private val colorBlue by intValue("color_blue", 0, 0..255)
    private val showDistance by boolValue("distance", true)
    private val showHealth by boolValue("health", true)

    fun render(canvas: Canvas) {
        if (!isEnabled || !isSessionCreated) return

        val localPlayer = session.localPlayer
        val players = session.level.entityMap.values.filterIsInstance<Player>()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.rgb(colorRed, colorGreen, colorBlue)
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 32f
            color = Color.rgb(colorRed, colorGreen, colorBlue)
            textAlign = Paint.Align.CENTER
        }

        players.forEach { player ->
            if (player != localPlayer) {
                drawPlayerBox(canvas, player, paint, textPaint)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun drawPlayerBox(canvas: Canvas, player: Player, paint: Paint, textPaint: Paint) {
        val x = 150f
        val y = 150f
        val width = 80f
        val height = 150f

        canvas.drawRect(x, y, x + width, y + height, paint)

        val distance = player.distance(session.localPlayer)
        val text = buildString {
            append(player.username)
            if (showDistance) append(" %.1fm".format(distance))
        }

        canvas.drawText(text, x + width / 2, y - 20, textPaint)

        if (showHealth) {
            val health = (player.health / player.maxHealth).coerceIn(0f, 1f)
            val healthBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = if (health > 0.5f) Color.GREEN else if (health > 0.25f) Color.YELLOW else Color.RED
            }
            val barWidth = width * health
            canvas.drawRect(x, y + height + 5, x + barWidth, y + height + 15, healthBar)
        }
    }
}