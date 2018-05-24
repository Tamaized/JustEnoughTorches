package net.insomniakitten.jetorches.fixer

import net.insomniakitten.jetorches.JETorchesMigration
import net.insomniakitten.jetorches.util.ResourceSupplier
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.datafix.IFixableData

data class BlockStateFixer(
        private val match: ResourceLocation,
        private val values: Array<out ResourceSupplier>
) : IFixableData {
    override fun getFixVersion() = JETorchesMigration.DATA_VERSION

    override fun fixTagCompound(compound: NBTTagCompound) = compound.apply {
        TODO()
    }

    override fun equals(other: Any?) = this === other || (other as? BlockStateFixer)
            ?.let { match == it.match && values.contentEquals(it.values) } ?: false

    override fun hashCode() = 31 * match.hashCode() + values.contentHashCode()

    override fun toString() = "BlockStateFixer { $match, [${values.joinToString(", ")}] }"
}
