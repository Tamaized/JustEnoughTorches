package net.insomniakitten.jetorches.light

import net.insomniakitten.jetorches.JETorches
import net.insomniakitten.jetorches.JETorchesConfig
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Loader.isModLoaded
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Function

private typealias LightFunction = Function<IBlockState, Int>

val IBlockState.sidedLightValue get() = SidedLightProxy.instance.apply(this)

@Suppress("unused", "DEPRECATION")
object SidedLightProxy {
    @SidedProxy(modId = JETorches.ID)
    lateinit var instance: LightFunction
        private set

    @SideOnly(Side.CLIENT)
    class ClientProxy : LightFunction {
        private val hasProvider by lazy { isModLoaded("mirage") || isModLoaded("albedo") }
        private val isEnabled get() = JETorchesConfig.coloredLighting
        private val isClient get() = Minecraft.getMinecraft().world.isRemote

        override fun apply(state: IBlockState) = if (hasProvider && isEnabled && isClient) 0 else state.lightValue
    }

    @SideOnly(Side.SERVER)
    class ServerProxy : LightFunction {
        override fun apply(state: IBlockState) = state.lightValue
    }
}
