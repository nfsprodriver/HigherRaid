import events.*
import org.bukkit.plugin.java.JavaPlugin

class HigherRaid : JavaPlugin(){

    override fun onEnable() {
        saveDefaultConfig()
        val maxAmp: Int = config.getInt("maxAmp") ?: 20
        val duration: Int = config.getInt("duration") ?: 120000
        val addProb: Double = config.getDouble("addProb") ?: 0.5
        server.pluginManager.registerEvents(EntitySpawn(this, addProb), this)
        server.pluginManager.registerEvents(PillagerKilled(this, maxAmp, duration), this)
        server.pluginManager.registerEvents(RaidTriggerEvent(this), this)
        server.pluginManager.registerEvents(RaidWaveEvent(this), this)
        server.pluginManager.registerEvents(RaidFinishEvent(duration, this), this)
    }
}