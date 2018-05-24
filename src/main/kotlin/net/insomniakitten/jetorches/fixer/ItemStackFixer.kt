package net.insomniakitten.jetorches.fixer

import net.insomniakitten.jetorches.JETorches.LOGGER
import net.insomniakitten.jetorches.JETorchesMigration
import net.insomniakitten.jetorches.util.ResourceSupplier
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.datafix.IFixableData
import net.minecraftforge.common.util.Constants.NBT

data class ItemStackFixer(
        private val match: ResourceLocation,
        private val values: Array<out ResourceSupplier>
) : IFixableData {
    override fun getFixVersion() = JETorchesMigration.DATA_VERSION

    override fun fixTagCompound(compound: NBTTagCompound) = compound.apply {
        if (hasKey("id", NBT.TAG_STRING) && hasKey("Damage", NBT.TAG_SHORT)) {
            if (getString("id") == match.toString()) {
                getShort("Damage").takeIf { it >= 0 && it < values.size }?.let {
                    val newId = values[it.toInt()].registryKey
                    LOGGER.debug("Migrating $match#$it to $newId")
                    setString("id", newId.toString())
                    setShort("Damage", 0)
                }
            }
        }
    }

    override fun equals(other: Any?) = this === other || (other as? ItemStackFixer)
            ?.let { match == it.match && values.contentEquals(it.values) } ?: false

    override fun hashCode() = 31 * match.hashCode() + values.contentHashCode()

    override fun toString() = "ItemStackFixer { $match, [${values.joinToString(", ")}] }"
}
