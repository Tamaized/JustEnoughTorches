package net.insomniakitten.jetorches.variant

import net.insomniakitten.jetorches.light.ColorSupplier
import net.insomniakitten.jetorches.util.ResourceSupplier

enum class LampVariant(
        val ore: String,
        override val color: Int,
        val hardness: Float,
        val resistance: Float,
        override val radius: Float = 6.0f
) : ResourceSupplier, ColorSupplier {
    LAPIS("blockLampLapis", 0x003BC0, 0.3f, 1.5f),
    OBSIDIAN("blockLampObsidian", 0x6300C0, 2.0f, 3000.0f),
    QUARTZ("blockLampQuartz", 0xFFFFFF, 0.5f, 3.0f);

    override val path: String get() = "lamp_${getName()}"
}
