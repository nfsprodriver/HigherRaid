package events

import org.bukkit.NamespacedKey
import org.bukkit.Raid
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.raid.RaidTriggerEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

class RaidTriggerEvent(private val plugin: Plugin, private val logger: Logger) : Listener {

    @EventHandler
    fun raid(event: RaidTriggerEvent) {
        val raid: Raid = event.raid
        val player: Player = event.player
        val lastBadOmenValueKey = NamespacedKey(plugin, "lastBadOmenValue")
        val lastBadOmenValue: Int? = player.persistentDataContainer.get(lastBadOmenValueKey, PersistentDataType.INTEGER)
        if (lastBadOmenValue != null) {
            val currentWaveKey = NamespacedKey(plugin, "currentWave")
            val wavesOverrideKey = NamespacedKey(plugin, "wavesOverride")
            raid.location.chunk.persistentDataContainer.set(wavesOverrideKey, PersistentDataType.INTEGER, lastBadOmenValue)
            raid.location.chunk.persistentDataContainer.set(currentWaveKey, PersistentDataType.INTEGER, 0)
            player.sendTitle("Raid will have $lastBadOmenValue waves.", "", 20, 100, 20)
            player.persistentDataContainer.remove(lastBadOmenValueKey)
        }
    }
}