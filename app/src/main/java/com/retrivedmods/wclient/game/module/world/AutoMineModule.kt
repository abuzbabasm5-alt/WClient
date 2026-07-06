package com.retrivedmods.wclient.game.module.player

import com.retrivedmods.wclient.game.InterceptablePacket
import com.retrivedmods.wclient.game.Module
import com.retrivedmods.wclient.game.ModuleCategory
import org.cloudburstmc.math.vector.Vector3i
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket

class AutoMineModule : Module("AutoMine", ModuleCategory.Player) {

    // Kazılacak örnek hedef koordinat (Test amaçlı, geliştirilmesi gerekir)
    private var targetBlockPos: Vector3i = Vector3i.from(0, 60, 0)
    private var isMining = false

    override fun onEnabled() {
        super.onEnabled()
        isMining = true
        // Modül açıldığında kazma işlemini başlat
        sendMineAction(PlayerActionType.START_BREAK, targetBlockPos, 0)
    }

    override fun onDisabled() {
        super.onDisabled()
        if (isMining) {
            // Modül kapatıldığında kazmayı iptal et
            sendMineAction(PlayerActionType.ABORT_BREAK, targetBlockPos, 0)
            isMining = false
        }
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled || !isMining) return

        // Blok kırma sürecini devam ettirmek için sürekli ağ paketlerini güncel tutabilirsiniz
        // Gelişmiş versiyonlarda oyuncunun baktığı blok (raycast) koordinatları dinamik olarak alınmalıdır.
    }

    private fun sendMineAction(actionType: PlayerActionType, pos: Vector3i, face: Int) {
        if (!isSessionCreated) return

        val actionPacket = PlayerActionPacket()
        actionPacket.action = actionType
        actionPacket.blockPosition = pos
        actionPacket.face = face
        actionPacket.runtimeEntityId = 0 // Yerel oyuncu ID'si ile senkronize edilmelidir

        session.serverBound(actionPacket)
    }
}
