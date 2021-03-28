package events

import org.bukkit.Raid
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.raid.RaidFinishEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.logging.Logger

class RaidFinishEvent(private val duration: Int, private val logger: Logger) : Listener {

    @EventHandler
    fun raidWave(event: RaidFinishEvent) {
        val raid: Raid = event.raid
        val raidBlock: Block = raid.location.block
        if (raidBlock.hasMetadata("wavesOverride")) {
            val totalWaves: Int = raidBlock.getMetadata("wavesOverride").first().asInt()
            val world = raid.location.world
            val players: List<Player> = world.players
            players.forEach { player ->
                if (player.location.distance(raid.location) < 100) {
                    val heroEffect = PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, totalWaves)
                    player.addPotionEffect(heroEffect)
                }
            }
        }
    }
}