package net.insomniakitten.jetorches.block.material

import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material

class TorchMaterial private constructor(private val canWorkUnderwater: Boolean) : Material(MapColor.AIR) {
    init {
        setAdventureModeExempt()
    }

    override fun isSolid() = false

    override fun blocksLight() = false

    override fun blocksMovement() = canWorkUnderwater

    override fun getMobilityFlag() =
            if (canWorkUnderwater) {
                EnumPushReaction.NORMAL
            } else EnumPushReaction.DESTROY

    companion object {
        val NORMAL = TorchMaterial(false)
        val UNDERWATER = TorchMaterial(true)
    }
}
