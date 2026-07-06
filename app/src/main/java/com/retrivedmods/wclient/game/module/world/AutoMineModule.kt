package com.retrivedmods.wclient.game.module.world

import com.retrivedmods.wclient.game.InterceptablePacket
import com.retrivedmods.wclient.game.Module
import com.retrivedmods.wclient.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket

class AutoMineModule : Module("auto_mine", ModuleCategory.World) {

    enum class OreMode { 
        ALL,
        DIAMONDS,
        IRON,
        GOLD,
        EMERALD
    }

    private val speed by intValue("speed", 8, 1..20)
    private val oreMode by enumValue("ore_mode", OreMode.ALL, OreMode::class.java)
    private var breakCounter = 0

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return
        
        val packet = interceptablePacket.packet
        if (packet is PlayerActionPacket) {
            breakCounter++
            if (breakCounter >= (20 - speed)) {
                breakCounter = 0
            }
        }
    }
}