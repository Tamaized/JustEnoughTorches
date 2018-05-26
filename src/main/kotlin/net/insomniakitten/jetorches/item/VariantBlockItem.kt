package net.insomniakitten.jetorches.item

import net.insomniakitten.jetorches.util.ResourceSupplier
import net.insomniakitten.jetorches.util.get
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.ForgeRegistries.BLOCKS

class VariantBlockItem(
        supplier: ResourceSupplier
) : ItemBlock(BLOCKS[supplier.registryKey]) {
    init {
        registryName = supplier.registryKey
    }
}
