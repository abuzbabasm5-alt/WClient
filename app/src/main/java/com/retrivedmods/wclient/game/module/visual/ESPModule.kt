package com.retrivedmods.wclient.game.module.visual

import android.annotation.SuppressLint
import android.graphics.*
import com.retrivedmods.wclient.game.InterceptablePacket
import com.retrivedmods.wclient.game.Module
import com.retrivedmods.wclient.game.ModuleCategory
import com.retrivedmods.wclient.game.entity.Entity
import com.retrivedmods.wclient.game.entity.LocalPlayer
import com.retrivedmods.wclient.game.entity.Player
import com.retrivedmods.wclient.render.RenderOverlayView
import org.cloudburstmc.math.matrix.Matrix4f
import org.cloudburstmc.math.vector.Vector2f
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.math.vector.Vector4f
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData
import org.cloudburstmc.protocol.bedrock.packet.MobArmorEquipmentPacket
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket

class ESPModule : Module("esp", ModuleCategory.Visual) {

    companion object {
        private var renderView: RenderOverlayView? = null
        fun setRenderView(view: RenderOverlayView) {
            renderView = view
        }
    }

    enum class BoxMode { None, Box2D, Box3D, Corner }

    private val colorRed by intValue("color_red", 230, 0..255)
    private val colorGreen by intValue("color_green", 57, 0..255)
    private val colorBlue by intValue("color_blue", 70, 0..255)

    private val showAllEntities by boolValue("show_all_entities", false)
    private val ignoreBots by boolValue("ignore_bots", true)
    private val boxMode by enumValue("box_mode", BoxMode.Box2D, BoxMode::class.java)
    private val showNames by boolValue("nametags", true)
    private val showDistance by boolValue("show_distance", true)
    private val showArmor by boolValue("show_armor", false)

    private fun Player.isBot(): Boolean {
        if (this is LocalPlayer) return false
        val playerListEntry = session.level.playerMap[this.uuid] ?: return true
        val name = playerListEntry.name?.toString() ?: ""
        return name.isBlank()
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        val packet = interceptablePacket.packet
        when (packet) {
            is MobArmorEquipmentPacket -> {
                val entity = session.level.entityMap.values.firstOrNull {
                    it.runtimeEntityId == packet.runtimeEntityId
                } ?: return
                entity.inventory.onPacketBound(packet)
            }
            is MobEquipmentPacket -> {
                val entity = session.level.entityMap.values.firstOrNull {
                    it.runtimeEntityId == packet.runtimeEntityId
                } ?: return
                entity.inventory.onPacketBound(packet)
            }
        }
    }

    override fun onEnabled() { renderView?.invalidate() }
    override fun onDisabled() { renderView?.invalidate() }

    fun render(canvas: Canvas) {
        if (!isEnabled || !isSessionCreated) return

        val localPlayer = session.localPlayer
        val entities = if (showAllEntities) {
            session.level.entityMap.values
        } else {
            session.level.entityMap.values.filterIsInstance<Player>()
        }

        if (entities.isEmpty()) return

        val filteredEntities = if (ignoreBots) {
            entities.filter { entity -> if (entity is Player) !entity.isBot() else true }
        } else {
            entities
        }

        if (filteredEntities.isEmpty()) return

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.rgb(colorRed, colorGreen, colorBlue)
        }

        filteredEntities.forEach { entity ->
            if (entity != localPlayer) {
                drawEntityBox(entity, canvas, paint)
            }
        }
    }

    private fun drawEntityBox(entity: Entity, canvas: Canvas, paint: Paint) {
        val x = 100f
        val y = 100f
        val width = 100f
        val height = 200f

        when (boxMode) {
            BoxMode.None -> {}
            BoxMode.Box2D -> canvas.drawRect(x, y, x + width, y + height, paint)
            BoxMode.Corner -> drawCornerBox(canvas, paint, x, y, x + width, y + height)
            BoxMode.Box3D -> {}
        }

        if (showNames || showDistance) {
            drawEntityInfo(canvas, entity, x, y, x + width)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun drawEntityInfo(canvas: Canvas, entity: Entity, minX: Float, minY: Float, maxX: Float) {
        val name = if (entity is Player) entity.username else "Entity"
        val distance = entity.distance(session.localPlayer)
        val x = (minX + maxX) / 2
        val y = minY - 30

        val text = buildString {
            append(name)
            if (showDistance) append(" [%.1fm]".format(distance))
        }

        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 36f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.rgb(colorRed, colorGreen, colorBlue)
        }

        canvas.drawText(text, x, y, namePaint)
    }

    private fun drawCornerBox(c: Canvas, p: Paint, minX: Float, minY: Float, maxX: Float, maxY: Float) {
        val cornerLen = 15f
        val stroke = Paint(p).apply { strokeWidth = 3f }

        c.drawLine(minX, minY, minX + cornerLen, minY, stroke)
        c.drawLine(minX, minY, minX, minY + cornerLen, stroke)
        c.drawLine(maxX, minY, maxX - cornerLen, minY, stroke)
        c.drawLine(maxX, minY, maxX, minY + cornerLen, stroke)
        c.drawLine(minX, maxY, minX + cornerLen, maxY, stroke)
        c.drawLine(minX, maxY, minX, maxY - cornerLen, stroke)
        c.drawLine(maxX, maxY, maxX - cornerLen, maxY, stroke)
        c.drawLine(maxX, maxY, maxX, maxY - cornerLen, stroke)
    }
}