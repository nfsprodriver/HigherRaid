package events

import org.bukkit.Raid
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.raid.RaidTriggerEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

class RaidTriggerEvent(private val plugin: Plugin, private val logger: Logger) : Listener {

    @EventHandler
    fun raid(event: RaidTriggerEvent) {
        val raid: Raid = event.raid
        val player: Player = event.player
        if (player.hasMetadata("lastBadOmenValue")) {
            val lastBadOmenValue: MetadataValue = player.getMetadata("lastBadOmenValue").first()
            val currentWaveMeta: MetadataValue = FixedMetadataValue(plugin, 0)
            raid.location.block.setMetadata("wavesOverride", lastBadOmenValue)
            raid.location.block.setMetadata("currentWave", currentWaveMeta)
            player.sendTitle("Raid will have " + lastBadOmenValue.asInt().toString() + " waves.", "", 20, 100, 20)
            player.removeMetadata("lastBadOmenValue", plugin)
        }
    }
}