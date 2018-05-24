package net.insomniakitten.jetorches

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object JETorchesMigration {
    private val OLD_TORCH = ResourceLocation(JETorches.ID, "torch")

    @SubscribeEvent
    fun onMissingItemMappings(event: RegistryEvent.MissingMappings<Item>) =
            event.mappings.firstOrNull { it.key == OLD_TORCH }?.run {
                TODO("Exploding one item into seperate items???")
            }
}
