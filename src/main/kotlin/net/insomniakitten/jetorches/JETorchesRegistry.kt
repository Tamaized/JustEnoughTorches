package net.insomniakitten.jetorches

import net.insomniakitten.jetorches.block.LampBlock
import net.insomniakitten.jetorches.block.TorchBlock
import net.insomniakitten.jetorches.item.VariantBlockItem
import net.insomniakitten.jetorches.item.VariantItem
import net.insomniakitten.jetorches.light.ColoredLight
import net.insomniakitten.jetorches.util.get
import net.insomniakitten.jetorches.variant.ItemVariant
import net.insomniakitten.jetorches.variant.LampVariant
import net.insomniakitten.jetorches.variant.TorchVariant
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary

object JETorchesRegistry {
    @SubscribeEvent
    fun onBlockRegistry(event: Register<Block>) = event.registry.run {
        GameRegistry.registerTileEntity(ColoredLight::class.java, ColoredLight.ID)
        TorchVariant.values().map(::TorchBlock).forEach(::register)
        LampVariant.values().map(::LampBlock).forEach(::register)
    }

    @SubscribeEvent
    fun onItemRegistry(event: Register<Item>) = event.registry.run {
        ItemVariant.values().map(::VariantItem).forEach(::register)
        TorchVariant.values().map(::VariantBlockItem).forEach(::register)
        LampVariant.values().map(::VariantBlockItem).forEach(::register)
    }

    @SubscribeEvent
    fun onRecipeRegistry(event: Register<IRecipe>) {
        fun register(key: ResourceLocation, vararg ores: String) = ItemStack(ITEMS[key]).let {
            ores.forEach { ore -> OreDictionary.registerOre(ore, it) }
        }

        ItemVariant.values().forEach { register(it.registryKey, it.ore) }
        TorchVariant.values().forEach { register(it.registryKey, "torch", it.ore) }
        LampVariant.values().forEach { register(it.registryKey, "blockLamp", it.ore) }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun onModelRegistry(event: ModelRegistryEvent) {
        fun register(key: ResourceLocation, variant: String) = ITEMS[key].let {
            ModelResourceLocation(it.registryName!!, variant).let { model ->
                ModelLoader.setCustomModelResourceLocation(it, 0, model)
            }
        }

        ItemVariant.values().forEach { register(it.registryKey, "inventory") }
        TorchVariant.values().forEach { register(it.registryKey, "inventory") }
        LampVariant.values().forEach { register(it.registryKey, "powered=false") }
    }
}
