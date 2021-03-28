import events.*
import org.bukkit.plugin.java.JavaPlugin

class HigherRaid : JavaPlugin(){

    override fun onEnable() {
        saveDefaultConfig()
        val maxAmp: Int = config.getInt("maxAmp") ?: 20
        val duration: Int = config.getInt("duration") ?: 120000
        server.pluginManager.registerEvents(EntitySpawn(logger), this)
        server.pluginManager.registerEvents(PillagerKilled(this, maxAmp, duration, logger), this)
        server.pluginManager.registerEvents(RaidTriggerEvent(this, logger), this)
        server.pluginManager.registerEvents(RaidWaveEvent(this, logger), this)
        server.pluginManager.registerEvents(RaidFinishEvent(duration, logger), this)
    }
}