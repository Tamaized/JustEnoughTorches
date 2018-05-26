package net.insomniakitten.jetorches.block.material

import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material

sealed class TorchMaterial(
        private val canWorkUnderwater: Boolean
) : Material(MapColor.AIR) {
    object Normal : TorchMaterial(false)
    object Underwater : TorchMaterial(true)

    override fun isSolid() = false

    override fun blocksLight() = false

    override fun blocksMovement() = canWorkUnderwater

    override fun getMobilityFlag() = EnumPushReaction.DESTROY
}
