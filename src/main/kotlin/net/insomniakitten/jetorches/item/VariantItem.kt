package net.insomniakitten.jetorches.item

import net.insomniakitten.jetorches.JETorches
import net.insomniakitten.jetorches.util.ResourceSupplier
import net.minecraft.item.Item

class VariantItem(
        supplier: ResourceSupplier
) : Item() {
    init {
        registryName = supplier.registryKey
        unlocalizedName = supplier.translationKey
        creativeTab = JETorches.TAB
    }
}
