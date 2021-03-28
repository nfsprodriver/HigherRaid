package events

import org.bukkit.Location
import org.bukkit.Raid
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.raid.RaidSpawnWaveEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

class RaidWaveEvent(private val plugin: Plugin, private val logger: Logger) : Listener {

    @EventHandler
    fun raidWave(event: RaidSpawnWaveEvent) {
        val raid: Raid = event.raid
        val raidBlock: Block = raid.location.block
        if (raidBlock.hasMetadata("currentWave")) {
            val currentWave: Int = raidBlock.getMetadata("currentWave").first().asInt()
            val currentWaveMeta: MetadataValue = FixedMetadataValue(plugin, currentWave + 1)
            raidBlock.setMetadata("currentWave", currentWaveMeta)
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