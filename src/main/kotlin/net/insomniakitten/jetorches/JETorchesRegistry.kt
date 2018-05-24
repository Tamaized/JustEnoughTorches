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
        ItemVariant.values().forEach {
            val item = ItemStack(ITEMS[it.registryKey])
            OreDictionary.registerOre(it.ore, item)
        }

        TorchVariant.values().forEach {
            val item = ItemStack(ITEMS[it.registryKey])
            OreDictionary.registerOre("torch", item)
            OreDictionary.registerOre(it.ore, item)
        }

        LampVariant.values().forEach {
            val item = ItemStack(ITEMS[it.registryKey])
            OreDictionary.registerOre("blockLamp", item)
            OreDictionary.registerOre(it.ore, item)
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun onModelRegistry(event: ModelRegistryEvent) {
        fun register(item: Item, variant: String) {
            val model = ModelResourceLocation(item.registryName!!, variant)
            ModelLoader.setCustomModelResourceLocation(item, 0, model)
        }

        ItemVariant.values().forEach { register(ITEMS[it.registryKey], "inventory") }
        TorchVariant.values().forEach { register(ITEMS[it.registryKey], "inventory") }
        LampVariant.values().forEach { register(ITEMS[it.registryKey], "powered=false") }
    }
}
