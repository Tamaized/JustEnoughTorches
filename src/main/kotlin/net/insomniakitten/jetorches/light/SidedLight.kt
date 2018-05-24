package net.insomniakitten.jetorches.light

import net.insomniakitten.jetorches.JETorches
import net.insomniakitten.jetorches.JETorchesConfig
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

fun IBlockState.getSidedLightValue(access: IBlockAccess, pos: BlockPos) =
        SidedLightProxy.instance(this, access, pos)

@Suppress("unused", "DEPRECATION")
object SidedLightProxy {
    private const val CLIENT = "net.insomniakitten.jetorches.light.SidedLightProxy\$ClientImpl"
    private const val SERVER = "net.insomniakitten.jetorches.light.SidedLightProxy\$Impl"

    @SidedProxy(modId = JETorches.ID, clientSide = CLIENT, serverSide = SERVER)
    internal lateinit var instance: Impl

    open class Impl {
        open operator fun invoke(state: IBlockState, access: IBlockAccess, pos: BlockPos) =
                state.lightValue
    }

    @SideOnly(Side.CLIENT)
    class ClientImpl : Impl() {
        private val hasProvider = Loader.isModLoaded("mirage") || Loader.isModLoaded("albedo")
        private val isEnabled get() = JETorchesConfig.coloredLighting
        private val isClient get() = Minecraft.getMinecraft().world.isRemote

        override fun invoke(state: IBlockState, access: IBlockAccess, pos: BlockPos) =
                if (hasProvider && isEnabled && isClient) 0 else state.lightValue
    }
}
