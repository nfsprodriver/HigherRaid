package events

import org.bukkit.Chunk
import org.bukkit.NamespacedKey
import org.bukkit.Raid
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.raid.RaidFinishEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.logging.Logger

class RaidFinishEvent(private val duration: Int, private val plugin: Plugin, private val logger: Logger) : Listener {

    @EventHandler
    fun raidWave(event: RaidFinishEvent) {
        val raid: Raid = event.raid
        val raidChunk: Chunk = raid.location.chunk
        val wavesOverrideKey = NamespacedKey(plugin, "wavesOverride")
        val totalWaves: Int? = raidChunk.persistentDataContainer.get(wavesOverrideKey, PersistentDataType.INTEGER)
        if (totalWaves != null) {
            val world = raid.location.world
            val players: List<Player> = world.players
            players.forEach { player ->
                if (player.location.distance(raid.location) < 100) {
                    val heroEffect = PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, totalWaves - 1)
                    player.addPotionEffect(heroEffect)
                }
            }
        }
    }
}