package net.insomniakitten.jetorches.block

import net.insomniakitten.jetorches.JETorches
import net.insomniakitten.jetorches.JETorchesConfig
import net.insomniakitten.jetorches.light.ColoredLight
import net.insomniakitten.jetorches.light.sidedLightValue
import net.insomniakitten.jetorches.util.get
import net.insomniakitten.jetorches.util.set
import net.insomniakitten.jetorches.util.with
import net.insomniakitten.jetorches.variant.LampVariant
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class LampBlock(
        val variant: LampVariant
) : Block(Material.GLASS) {
    init {
        registryName = variant.registryKey
        unlocalizedName = variant.translationKey
        soundType = SoundType.GLASS
        setHardness(variant.hardness)
        setResistance(variant.resistance)
        setCreativeTab(JETorches.TAB)
    }

    override fun createBlockState() = BlockStateContainer(this, POWERED)

    override fun getStateFromMeta(meta: Int) = defaultState.with(POWERED, meta != 0)

    override fun getMetaFromState(state: IBlockState) = if (state[POWERED]) 1 else 0

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, neighbor: BlockPos) =
            updatePoweredState(state, world, pos)

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) =
            updatePoweredState(state, world, pos)

    override fun getLightValue(state: IBlockState, access: IBlockAccess, pos: BlockPos) =
            if (state[POWERED]) state.sidedLightValue else 0

    override fun hasTileEntity(state: IBlockState) = JETorchesConfig.coloredLighting && state[POWERED]

    override fun createTileEntity(world: World, state: IBlockState) = variant.run {
        if (hasTileEntity(state)) ColoredLight(color, radius) else null
    }

    private fun updatePoweredState(state: IBlockState, world: World, pos: BlockPos) {
        if (!world.isRemote) world.isBlockPowered(pos).let {
            if (it != state[POWERED]) world[pos] = state.with(POWERED, it) to 2
        }
    }

    companion object {
        private val POWERED = PropertyBool.create("powered")
    }
}
