package net.insomniakitten.jetorches.item

import net.insomniakitten.jetorches.JETorches
import net.insomniakitten.jetorches.variant.ItemVariant
import net.minecraft.item.Item

class VariantItem(it: ItemVariant) : Item() {
    init {
        registryName = it.registryKey
        unlocalizedName = it.translationKey
        creativeTab = JETorches.TAB
    }
}
