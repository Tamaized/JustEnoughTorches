package net.insomniakitten.jetorches

import net.insomniakitten.jetorches.block.TorchBlock
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object JETorchesEvents {
    private val Entity.isUnderwater get() =
        isInsideOfMaterial(Material.WATER)

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun onViewRenderFOVModifier(event: EntityViewRenderEvent.FOVModifier) {
        (event.state.block as? TorchBlock)?.run {
            if (variant.canWorkUnderwater && event.entity.isUnderwater) {
                event.fov *= 60.0F / 70.0F
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun onViewRenderFogDensity(event: EntityViewRenderEvent.FogDensity) {
        (event.state.block as? TorchBlock)?.run {
            if (variant.canWorkUnderwater && event.entity.isUnderwater) {
                event.isCanceled = true
                GlStateManager.setFog(GlStateManager.FogMode.EXP)
                event.density = 0.115F
            }
        }
    }
}
