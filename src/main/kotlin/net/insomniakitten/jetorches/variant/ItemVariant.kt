package net.insomniakitten.jetorches.variant

import net.insomniakitten.jetorches.util.ResourceSupplier

enum class ItemVariant(
        val ore: String
) : ResourceSupplier {
    STICK_STONE("stickStone"),
    STICK_NETHER("stickNetherrack"),
    STICK_PRISMARINE("stickPrismarine"),
    STICK_OBSIDIAN("stickObsidian"),
    STICK_GOLDEN("stickGold");
}
