package net.insomniakitten.jetorches.variant

import net.insomniakitten.jetorches.JETorchesConfig
import net.insomniakitten.jetorches.block.material.TorchMaterial
import net.insomniakitten.jetorches.light.ColorSupplier
import net.insomniakitten.jetorches.util.ResourceSupplier
import net.minecraft.block.SoundType
import net.minecraft.util.EnumParticleTypes

enum class TorchVariant(
        val ore: String,
        private val _light: Int,
        override val color: Int,
        val sound: SoundType,
        val particle: EnumParticleTypes,
        val hardness: Float = 0.0F,
        val resistance: Float = 0.0F,
        val canWorkUnderwater: Boolean = false
) : ResourceSupplier, ColorSupplier {
    STONE("torchStone", 14, 0xFFDE6C, SoundType.STONE, EnumParticleTypes.FLAME),
    NETHER("torchNetherrack", 10, 0xFF7200, SoundType.STONE, EnumParticleTypes.SMOKE_NORMAL),
    PRISMARINE(
            "torchPrismarine", 15, 0x66E8E0, SoundType.GLASS, EnumParticleTypes.WATER_DROP,
            canWorkUnderwater = JETorchesConfig.prismarineUnderwater
    ),
    OBSIDIAN("torchObsidian", 13, 0x6300C0, SoundType.STONE, EnumParticleTypes.FLAME, 0.2f, 3000.0f),
    GOLDEN("torchGold", 14, 0xEBBC3C, SoundType.METAL, EnumParticleTypes.FLAME, 0.0f, 30.0f);

    val material = if (canWorkUnderwater) {
        TorchMaterial.Underwater
    } else TorchMaterial.Normal

    override val radius = light * 6.0F

    val light get() = 1.0F / 15.0F * _light

    override val path: String get() = "torch_${getName()}"
}
