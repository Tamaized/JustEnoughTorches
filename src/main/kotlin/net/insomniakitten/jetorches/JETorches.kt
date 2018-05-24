package net.insomniakitten.jetorches

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(
        modid = JETorches.ID,
        name = JETorches.NAME,
        version = JETorches.VERSION,
        acceptedMinecraftVersions = JETorches.MC_VERSIONS,
        dependencies = JETorches.DEPENDENCIES,
        modLanguageAdapter = JETorches.LANG_ADAPTER
)
object JETorches {
    const val ID = "jetorches"
    const val NAME = "Just Enough Torches"
    const val VERSION = "%VERSION%"
    const val MC_VERSIONS = "[1.12,1.13)"
    const val DEPENDENCIES = "required-after:forgelin@[%FORGELIN%,)"
    const val LANG_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    val LOGGER: Logger = LogManager.getLogger()

    @GameRegistry.ItemStackHolder("$ID:torch_stone")
    @JvmField val ICON: ItemStack = ItemStack.EMPTY

    val TAB: CreativeTabs = object : CreativeTabs(ID) {
        @SideOnly(Side.CLIENT)
        override fun getTranslatedTabLabel() = "item_group.$ID.label"

        @SideOnly(Side.CLIENT)
        override fun getTabIconItem() = ICON
    }

    init {
        MinecraftForge.EVENT_BUS.register(JETorchesConfig)
        MinecraftForge.EVENT_BUS.register(JETorchesEvents)
        MinecraftForge.EVENT_BUS.register(JETorchesRegistry)
        MinecraftForge.EVENT_BUS.register(JETorchesMigration)
    }
}
