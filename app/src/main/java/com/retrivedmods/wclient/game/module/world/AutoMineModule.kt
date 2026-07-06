package com.retrivedmods.wclient.game.module.player

import com.retrivedmods.wclient.game.Module
import com.retrivedmods.wclient.game.ModuleCategory
import com.retrivedmods.wclient.game.entity.Player

class AutoMineModule : Module("AutoMine", ModuleCategory.Misc) {

    enum class TargetOre(val blockName: String) {
        All("all_ores"),
        Diamond("diamond_ore"),
        Gold("gold_ore"),
        Iron("iron_ore"),
        AncientDebris("ancient_debris"),
        Coal("coal_ore")
    }

    private val targetOre by enumValue("target_ore", TargetOre.Diamond, TargetOre::class.java)
    private val mineRadius by floatValue("radius", 4.5f, 1f..7f)
    private val autoSwitchPickaxe by boolValue("auto_pickaxe", true)

    override fun onEnabled() {}
    override fun onDisabled() {}

    fun onTick() {
        if (!isEnabled || !isSessionCreated) return
        val currentTarget = targetOre
        val radius = mineRadius
    }
}
