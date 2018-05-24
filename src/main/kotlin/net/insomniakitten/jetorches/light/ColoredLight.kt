package net.insomniakitten.jetorches.light

import com.elytradev.mirage.event.GatherLightsEvent
import com.elytradev.mirage.lighting.ILightEventConsumer
import elucent.albedo.lighting.ILightProvider
import net.insomniakitten.jetorches.JETorches
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.common.Optional.Interface
import net.minecraftforge.fml.common.Optional.InterfaceList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

private typealias MirageLight = com.elytradev.mirage.lighting.Light
private typealias AlbedoLight = elucent.albedo.lighting.Light

@InterfaceList(
        Interface(modid = "mirage", striprefs = true, iface = "com.elytradev.mirage.lighting.ILightEventConsumer"),
        Interface(modid = "albedo", striprefs = true, iface = "elucent.albedo.lighting.ILightProvider")
)
class ColoredLight : TileEntity, ILightEventConsumer, ILightProvider {
    private var red = 0.0F
    private var green = 0.0F
    private var blue = 0.0F
    private var radius = 0.0F

    @Suppress("unused") constructor()

    constructor(rgb: Int, radius: Float) {
        red = (rgb shr 16 and 0xFF).toFloat()
        green = (rgb shr 8 and 0xFF).toFloat()
        blue = (rgb and 0xFF).toFloat()
        this.radius = radius
    }

    override fun readFromNBT(compound: NBTTagCompound) =
            super.readFromNBT(compound.apply {
                getCompoundTag("color").apply {
                    red = getFloat("r")
                    green = getFloat("g")
                    blue = getFloat("b")
                }
                radius = getFloat("radius")
            })

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound =
            super.writeToNBT(compound).apply {
                setTag("color", NBTTagCompound().apply {
                    setFloat("r", red)
                    setFloat("g", green)
                    setFloat("b", blue)
                })
                setFloat("radius", radius)
            }

    override fun getUpdatePacket() = SPacketUpdateTileEntity(pos, 0, updateTag)

    override fun getUpdateTag() = writeToNBT(super.getUpdateTag())

    override fun getDisplayName() = TextComponentTranslation(
            "${blockType?.unlocalizedName}.name"
    )

    @SideOnly(Side.CLIENT)
    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) =
            readFromNBT(pkt.nbtCompound)

    @SideOnly(Side.CLIENT)
    override fun gatherLights(event: GatherLightsEvent) = event.add(
            MirageLight.builder()
                    .color(red, green, blue, 2.0F)
                    .radius(radius)
                    .pos(pos)
                    .build()
    )

    @SideOnly(Side.CLIENT)
    override fun provideLight(): AlbedoLight =
            AlbedoLight.builder()
                    .color(red, green, blue, 2.0F)
                    .radius(radius)
                    .pos(pos)
                    .build()

    companion object {
        val ID = ResourceLocation(JETorches.ID, "colored_light")
    }
}
