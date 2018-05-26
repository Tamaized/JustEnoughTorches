package net.insomniakitten.jetorches.block

import net.insomniakitten.jetorches.JETorches
import net.insomniakitten.jetorches.JETorchesConfig
import net.insomniakitten.jetorches.light.ColoredLight
import net.insomniakitten.jetorches.light.sidedLightValue
import net.insomniakitten.jetorches.util.get
import net.insomniakitten.jetorches.util.getAllInRange
import net.insomniakitten.jetorches.util.invoke
import net.insomniakitten.jetorches.util.set
import net.insomniakitten.jetorches.util.with
import net.insomniakitten.jetorches.variant.TorchVariant
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.material.Material.WATER
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockFaceShape.SOLID
import net.minecraft.block.state.BlockFaceShape.UNDEFINED
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.Blocks.AIR
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.DOWN
import net.minecraft.util.EnumFacing.EAST
import net.minecraft.util.EnumFacing.NORTH
import net.minecraft.util.EnumFacing.Plane
import net.minecraft.util.EnumFacing.SOUTH
import net.minecraft.util.EnumFacing.UP
import net.minecraft.util.EnumFacing.WEST
import net.minecraft.util.EnumParticleTypes.FLAME
import net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL
import net.minecraft.util.Mirror
import net.minecraft.util.Rotation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

class TorchBlock(
        val variant: TorchVariant
) : Block(variant.material) {
    init {
        registryName = variant.registryKey
        unlocalizedName = variant.translationKey
        soundType = variant.sound
        setHardness(variant.hardness)
        setResistance(variant.resistance)
        setLightLevel(variant.light)
        setCreativeTab(JETorches.TAB)
        tickRandomly = true
    }

    override fun getMaterial(state: IBlockState) = variant.material

    override fun getStateFromMeta(meta: Int) = defaultState.with(FACING, VALUES[meta and 7])

    override fun getMetaFromState(state: IBlockState) = state[FACING].ordinal - 1

    override fun withRotation(state: IBlockState, rotation: Rotation) = state with rotation

    override fun withMirror(state: IBlockState, mirror: Mirror) = state with mirror

    override fun isFullCube(state: IBlockState) = false

    override fun getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos) =
            BOUNDING_BOXES[state[FACING]]!!

    override fun getBlockFaceShape(access: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing) =
            UNDEFINED

    override fun getCollisionBoundingBox(state: IBlockState, access: IBlockAccess, pos: BlockPos) =
            NULL_AABB

    override fun isOpaqueCube(state: IBlockState) = false

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(state: IBlockState, world: World, pos: BlockPos, rand: Random) {
        if (world.isRemote) state[FACING].run {
            var x = pos.x + 0.5
            var y = pos.y + 0.7
            var z = pos.z + 0.5

            if (axis.isHorizontal) {
                x += 0.27 + frontOffsetX
                y += 0.22
                z += 0.27 + frontOffsetZ
            }

            world.spawnParticle(variant.particle, x, y, z, 0.0, 0.0, 0.0)

            if (variant.particle == FLAME) {
                world.spawnParticle(SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0)
            }
        }
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) =
            state[FACING].run {
                if (axis.isHorizontal && !isSolid(world, pos(opposite), this) ||
                    axis.isVertical && !canPlaceOn(world, pos(opposite))) {
                    dropBlockAsItem(world, pos, state, 0)
                    world[pos] = AIR.defaultState
                }
            }

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
        if (!canPlaceAt(world, pos, state[FACING])) {
            dropBlockAsItem(world, pos, state, 0)
            world[pos] = AIR.defaultState
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer() = BlockRenderLayer.CUTOUT

    override fun canPlaceBlockAt(world: World, pos: BlockPos) = VALUES.any { canPlaceAt(world, pos, it) }

    override fun getStateForPlacement(world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase) =
            if (canPlaceAt(world, pos, side)) defaultState.with(FACING, side) else {
                defaultState.with(FACING, Plane.HORIZONTAL.firstOrNull {
                    canPlaceAt(world, pos, it)
                } ?: UP)
            }

    override fun createBlockState() = BlockStateContainer(this, FACING)

    override fun getLightValue(state: IBlockState, access: IBlockAccess, pos: BlockPos) =
            state.sidedLightValue

    override fun doesSideBlockRendering(state: IBlockState, access: IBlockAccess, pos: BlockPos, side: EnumFacing) =
            access[pos(side)].material.isLiquid && access[pos.up()].material.isLiquid

    override fun hasTileEntity(state: IBlockState) = JETorchesConfig.coloredLighting

    override fun createTileEntity(world: World, state: IBlockState) = variant.run {
        if (hasTileEntity(state)) ColoredLight(color, radius) else null
    }

    override fun isEntityInsideMaterial(access: IBlockAccess, pos: BlockPos, state: IBlockState, entity: Entity, yToTest: Double, material: Material, checkHead: Boolean) =
            if (variant.canWorkUnderwater && access[pos.up()].material == WATER) {
                if (pos.getAllInRange(1, 0, 1).any { access[it].material == WATER }) {
                    material == WATER
                } else null
            } else null

    // TODO: Cleanup
    override fun getFogColor(world: World, pos: BlockPos, state: IBlockState, entity: Entity, lastColor: Vec3d, partialTicks: Float): Vec3d {
        var posAt = pos
        var stateAt = state
        if (variant.canWorkUnderwater) {
            posAt = posAt.up()
            stateAt = world[posAt]
            if (stateAt.material.isLiquid) {
                var height = 0.0F
                if (stateAt.block is BlockLiquid) {
                    var meta = stateAt[BlockLiquid.LEVEL]
                    if (meta >= 8) meta = 0
                    height = (meta + 1).toFloat() / 9.0F - 0.11111111F
                }
                if (ActiveRenderInfo.projectViewFromEntity(entity, partialTicks.toDouble()).y > posAt.y + 1 - height) {
                    val upPos = posAt.up()
                    val upState = world[upPos]
                    return upState.block.getFogColor(world, upPos, upState, entity, lastColor, partialTicks)
                }
            }
        }
        return super.getFogColor(world, posAt, stateAt, entity, lastColor, partialTicks)
    }

    private fun canPlaceOn(access: IBlockAccess, pos: BlockPos) = access[pos].let {
        it.block.canPlaceTorchOnTop(it, access, pos)
    }

    private fun isSolid(world: World, pos: BlockPos, side: EnumFacing) =
            world[pos].getBlockFaceShape(world, pos, side) == SOLID

    private fun canPlaceAt(world: World, pos: BlockPos, side: EnumFacing) =
            side != DOWN && pos(side.opposite).let {
                canPlaceOn(world, it) && isSolid(world, it, side)
            }

    companion object {
        private val FACING = PropertyDirection.create("facing") { it != DOWN }

        private val VALUES = FACING.allowedValues.toTypedArray()

        private val BOUNDING_BOXES = mapOf(
                UP to AxisAlignedBB(0.40, 0.00, 0.40, 0.60, 0.60, 0.60),
                NORTH to AxisAlignedBB(0.35, 0.20, 0.70, 0.65, 0.80, 1.00),
                SOUTH to AxisAlignedBB(0.35, 0.20, 0.00, 0.65, 0.80, 0.30),
                WEST to AxisAlignedBB(0.70, 0.20, 0.35, 1.00, 0.80, 0.65),
                EAST to AxisAlignedBB(0.00, 0.20, 0.35, 0.30, 0.80, 0.65)
        )
    }
}
