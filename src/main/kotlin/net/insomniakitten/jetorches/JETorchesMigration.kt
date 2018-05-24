package net.insomniakitten.jetorches

import net.insomniakitten.jetorches.JETorches.LOGGER
import net.insomniakitten.jetorches.fixer.BlockStateFixer
import net.insomniakitten.jetorches.fixer.ItemStackFixer
import net.insomniakitten.jetorches.variant.LampVariant
import net.insomniakitten.jetorches.variant.TorchVariant
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraft.util.datafix.FixTypes.CHUNK
import net.minecraft.util.datafix.FixTypes.ITEM_INSTANCE
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object JETorchesMigration {
    const val DATA_VERSION = 1

    private val OLD_TORCH = ResourceLocation(JETorches.ID, "torch")
    private val OLD_LAMP = ResourceLocation(JETorches.ID, "lamp")

    private val fixer by lazy {
        FMLCommonHandler.instance().dataFixer.init(JETorches.ID, DATA_VERSION)
    }

    init {
        fixer.run {
            registerFix(CHUNK, BlockStateFixer(OLD_LAMP, LampVariant.values()))
            registerFix(ITEM_INSTANCE, ItemStackFixer(OLD_TORCH, TorchVariant.values()))
            registerFix(ITEM_INSTANCE, ItemStackFixer(OLD_LAMP, LampVariant.values()))
        }
    }

    @SubscribeEvent
    fun onMissingBlockMappings(event: RegistryEvent.MissingMappings<Block>) {
        event.mappings.firstOrNull { it.key == OLD_LAMP }?.run {
            LOGGER.debug("Skipping missing mapping $key as it will be migrated")
            ignore()
        }
    }

    @SubscribeEvent
    fun onMissingItemMappings(event: RegistryEvent.MissingMappings<Item>) {
        event.mappings.firstOrNull { it.key == OLD_TORCH || it.key == OLD_LAMP }?.run {
            LOGGER.debug("Skipping missing mapping $key as it will be migrated")
            ignore()
        }
    }
}
