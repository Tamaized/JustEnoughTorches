package net.insomniakitten.jetorches.util

import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.Mirror
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

fun <V : Comparable<V>> IBlockState.with(property: IProperty<V>, value: V): IBlockState =
        withProperty(property, value)

fun IBlockState.with(rotation: Rotation): IBlockState =
        withRotation(rotation)

fun IBlockState.with(mirror: Mirror): IBlockState =
        withMirror(mirror)

operator fun <V : Comparable<V>> IBlockState.get(property: IProperty<V>): V =
        getValue(property)

operator fun IBlockAccess.get(pos: BlockPos): IBlockState =
        getBlockState(pos)

operator fun World.set(pos: BlockPos, state: IBlockState) =
        setBlockState(pos, state)

operator fun World.set(pos: BlockPos, pair: Pair<IBlockState, Int>) =
        setBlockState(pos, pair.first, pair.second)

operator fun BlockPos.invoke(side: EnumFacing): BlockPos =
        offset(side)

operator fun <V : IForgeRegistryEntry<V>> IForgeRegistry<V>.get(key: ResourceLocation) =
        getValue(key) ?: throw NoSuchElementException("Unable to find registry entry for key $key")
