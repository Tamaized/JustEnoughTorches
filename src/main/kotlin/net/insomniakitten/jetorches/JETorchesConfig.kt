package net.insomniakitten.jetorches

import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.Config.Comment
import net.minecraftforge.common.config.Config.Name
import net.minecraftforge.common.config.Config.RequiresMcRestart
import net.minecraftforge.common.config.Config.RequiresWorldRestart
import net.minecraftforge.common.config.Config.Type
import net.minecraftforge.common.config.ConfigManager.sync
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Config(modid = JETorches.ID, name = JETorches.ID)
object JETorchesConfig {
    @Name("underwater_torches")
    @Comment("If true, the prismarine torch will not break when placed in water.")
    @RequiresMcRestart
    var prismarineUnderwater = true

    @Name("colored_lighting")
    @Comment("If true, torches will produce colored lighting when Mirage/Albedo is present.")
    @RequiresWorldRestart
    var coloredLighting = true

    @SubscribeEvent
    fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (JETorches.ID == event.modID) sync(JETorches.ID, Type.INSTANCE)
    }
}
