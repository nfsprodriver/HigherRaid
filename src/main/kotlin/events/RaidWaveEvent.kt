package events

import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.raid.RaidSpawnWaveEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class RaidWaveEvent(private val plugin: Plugin) : Listener {

    @EventHandler
    fun raidWave(event: RaidSpawnWaveEvent) {
        val raid: Raid = event.raid
        val raidChunk: Chunk = raid.location.chunk
        val currentWaveKey = NamespacedKey(plugin, "currentWave")
        val currentWave: Int? = raidChunk.persistentDataContainer.get(currentWaveKey, PersistentDataType.INTEGER)
        if (currentWave != null) {
            raidChunk.persistentDataContainer.set(currentWaveKey, PersistentDataType.INTEGER, currentWave + 1)
            val world = raid.location.world
            val players: List<Player> = world.players
            players.forEach { player ->
                if (player.location.distance(raid.location) < 100) {
                    player.sendTitle("Wave " + (currentWave + 1).toString(), "", 20, 100, 20)
                }
            }
        }
    }
}