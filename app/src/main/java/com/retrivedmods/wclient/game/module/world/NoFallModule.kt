package com.retrivedmods.wclient.game.module.motion

import com.retrivedmods.wclient.game.InterceptablePacket
import com.retrivedmods.wclient.game.Module
import com.retrivedmods.wclient.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket

class NoFallModule : Module("NoFall", ModuleCategory.Motion) {

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled || !isSessionCreated) return
        
        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            // Sunucuya sürekli yerdeymişiz bilgisi göndererek düşme hasarını engeller
            packet.motion = packet.motion?.run { this }
        } else if (packet is MovePlayerPacket) {
            packet.isOnGround = true
        }
    }
}
