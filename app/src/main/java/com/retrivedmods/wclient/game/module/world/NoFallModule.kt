package com.retrivedmods.wclient.game.module.movement

import com.retrivedmods.wclient.game.InterceptablePacket
import com.retrivedmods.wclient.game.Module
import com.retrivedmods.wclient.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket

class NoFallModule : Module("NoFall", ModuleCategory.Movement) {

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet

        // Yeni nesil Bedrock sunucularının kullandığı ana girdi paketi
        if (packet is PlayerAuthInputPacket) {
            // Sunucuya karakterin havada değil, yerde olduğunu söylüyoruz
            packet.onGround = true
        }
        
        // Eski nesil veya bazı özel sunucuların kullandığı hareket paketi
        if (packet is MovePlayerPacket) {
            // Aynı şekilde bu paketteki yerde olma durumunu da true yapıyoruz
            packet.isOnGround = true
        }
    }
}
