package net.insomniakitten.jetorches.util

import net.insomniakitten.jetorches.JETorches
import net.minecraft.util.IStringSerializable
import net.minecraft.util.ResourceLocation
import java.util.Locale

interface ResourceSupplier : IStringSerializable {
    val domain get() = JETorches.ID
    val path get() = name

    val registryKey get() = ResourceLocation(domain, path)
    val translationKey get() = "$domain.$path"

    override fun getName() = (this as Enum<*>).name.toLowerCase(Locale.ROOT)
}
