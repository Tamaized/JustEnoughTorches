package net.insomniakitten.jetorches.block

import net.insomniakitten.jetorches.JETorches
import net.insomniakitten.jetorches.JETorchesConfig
import net.insomniakitten.jetorches.light.ColoredLight
import net.insomniakitten.jetorches.light.getSidedLightValue
import net.insomniakitten.jetorches.util.get
import net.insomniakitten.jetorches.util.invoke
import net.insomniakitten.jetorches.util.with
import net.insomniakitten.jetorches.variant.TorchVariant
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
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
import net.minecraft.util.math.Vec3i
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

class TorchBlock(val variant: TorchVariant) : Block(variant.material) {
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

    override fun withRotation(state: IBlockState, rot: Rotation) = state.with(rot)

    override fun withMirror(state: IBlockState, mirror: Mirror) = state.with(mirror)

    override fun isFullCube(state: IBlockState) = false

    override fun getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos) =
            BOUNDING_BOXES[state[FACING]]!!

    override fun getBlockFaceShape(access: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing) =
            BlockFaceShape.UNDEFINED

    override fun getCollisionBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos) =
            Block.NULL_AABB

    override fun isOpaqueCube(state: IBlockState) = false

    // TODO: Cleanup
    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(state: IBlockState, world: World, pos: BlockPos, rand: Random) {
        if (!world.isRemote) return

        val facing = state[FACING]

        var x = pos.x + 0.5
        var y = pos.y + 0.7
        var z = pos.z + 0.5

        if (facing.axis.isHorizontal) {
            facing.opposite.run {
                x += 0.27 * frontOffsetX
                y += 0.22
                z += 0.27 * frontOffsetZ
            }
        }

        world.spawnParticle(variant.particle, x, y, z, 0.0, 0.0, 0.0)

        if (variant.particle == FLAME) {
            world.spawnParticle(SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0)
        }
    }

    // TODO: Cleanup
    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
        val facing = state[FACING]
        val offset = pos.offset(facing.opposite)
        if (facing.axis.isHorizontal && !isSolid(world, offset, facing) ||
            facing.axis.isVertical && !canPlaceOn(world, offset)) {
            dropBlockAsItem(world, pos, state, 0)
            world.setBlockToAir(pos)
        }
    }

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
        if (!canPlaceAt(world, pos, state[FACING])) {
            dropBlockAsItem(world, pos, state, 0)
            world.setBlockToAir(pos)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer() = BlockRenderLayer.CUTOUT

    override fun canPlaceBlockAt(world: World, pos: BlockPos) = VALUES.any { canPlaceAt(world, pos, it) }

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase) =
            when {
                canPlaceAt(world, pos, facing) -> defaultState.with(FACING, facing)
                else -> defaultState.with(FACING, Plane.HORIZONTAL.firstOrNull {
                    canPlaceAt(world, pos, it)
                } ?: UP)
            }

    override fun getMobilityFlag(state: IBlockState) = EnumPushReaction.DESTROY

    override fun createBlockState() = BlockStateContainer(this, FACING)

    override fun getLightValue(state: IBlockState, access: IBlockAccess, pos: BlockPos) =
            state.getSidedLightValue(access, pos)

    override fun doesSideBlockRendering(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing) =
            world[pos(side)].material.isLiquid && world[pos.up()].material.isLiquid

    override fun hasTileEntity(state: IBlockState) = JETorchesConfig.coloredLighting

    override fun createTileEntity(world: World, state: IBlockState) =
            if (hasTileEntity(state)) {
                ColoredLight(variant.color, variant.radius)
            } else null

    override fun isEntityInsideMaterial(access: IBlockAccess, pos: BlockPos, state: IBlockState, entity: Entity, yToTest: Double, material: Material, checkHead: Boolean) =
            if (variant.canWorkUnderwater && access[pos.up()].material == Material.WATER) {
                val offset = Vec3i(1, 0, 1)
                if (BlockPos.getAllInBoxMutable(pos.subtract(offset), pos.add(offset))
                                .any { access[it].material == Material.WATER }
                ) material == Material.WATER else null
            } else null

    // TODO: Cleanup
    override fun getFogColor(world: World, pos: BlockPos, state: IBlockState, entity: Entity, lastColor: Vec3d, partialTicks: Float): Vec3d {
        var posAt = pos
        var stateAt = state
        if (variant.canWorkUnderwater) {
            posAt = posAt.up()
            stateAt = world[posAt]
            if (stateAt.material.isLiquid) {
                var height = 0.0f
                if (stateAt.block is BlockLiquid) {
                    var meta = stateAt[BlockLiquid.LEVEL]
                    if (meta >= 8) meta = 0
                    height = (meta + 1).toFloat() / 9.0f - 0.11111111F
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

    private fun canPlaceOn(access: IBlockAccess, pos: BlockPos) =
            access[pos].let { it.block.canPlaceTorchOnTop(it, access, pos) }

    private fun isSolid(world: World, pos: BlockPos, facing: EnumFacing) =
            world[pos].getBlockFaceShape(world, pos, facing) == BlockFaceShape.SOLID

    private fun canPlaceAt(world: World, pos: BlockPos, side: EnumFacing) =
            side != DOWN && pos(side.opposite).let {
                canPlaceOn(world, it) && isSolid(world, it, side)
            }

    companion object {
        private val FACING = PropertyDirection.create("facing") { it != DOWN }

        private val VALUES = FACING.allowedValues.toTypedArray<EnumFacing>()

        private val BOUNDING_BOXES = mapOf(
                UP to AxisAlignedBB(0.40, 0.00, 0.40, 0.60, 0.60, 0.60),
                NORTH to AxisAlignedBB(0.35, 0.20, 0.70, 0.65, 0.80, 1.00),
                SOUTH to AxisAlignedBB(0.35, 0.20, 0.00, 0.65, 0.80, 0.30),
                WEST to AxisAlignedBB(0.70, 0.20, 0.35, 1.00, 0.80, 0.65),
                EAST to AxisAlignedBB(0.00, 0.20, 0.35, 0.30, 0.80, 0.65)
        )
    }
}
