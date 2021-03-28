package events

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.logging.Logger
import kotlin.math.roundToInt

class PillagerKilled(private val plugin: Plugin, private val maxAmp: Int, private val duration: Int, private val logger: Logger) : Listener {

    @EventHandler
    fun entityKill(event: EntityDeathEvent) {
        val entity: LivingEntity = event.entity
        if (entity.category == EntityCategory.ILLAGER) { // && entity.customName == "Captain") {
            val player: Player? = entity.killer
            if (player != null) {
                logger.info(player.name + " killed a raid captain.")
                player.world.raids.forEach { raid ->
                    if (player.location.distance(raid.location) < 100) {
                        val raidBlock: Block = raid.location.block
                        if (raidBlock.hasMetadata("currentWave") && raidBlock.hasMetadata("wavesOverride")) {
                            val currentWave: Int = raidBlock.getMetadata("currentWave").first().asInt()
                            val wavesOverride: Int = raidBlock.getMetadata("wavesOverride").first().asInt()
                            val raiders: List<Raider?> = raid.raiders
                            if (currentWave > raid.totalWaves && currentWave < wavesOverride && raiders.count() < 2) {
                                val currentWaveMeta: MetadataValue = FixedMetadataValue(plugin, currentWave + 1)
                                raidBlock.setMetadata("currentWave", currentWaveMeta)
                                spawnCustomWave(currentWave + 1, raid.location, logger)
                                player.sendTitle("Wave " + (currentWave + 1).toString(), "", 20, 100, 20)
                            }
                        }
                        return
                    }
                }
                var lastBadOmenValue = 0
                if (player.hasMetadata("lastBadOmenValue")) {
                    lastBadOmenValue = player.getMetadata("lastBadOmenValue").first().asInt()
                    if (lastBadOmenValue < maxAmp) {
                        lastBadOmenValue++
                    }
                }
                //val lastBadOmenMeta: MetadataValue = FixedMetadataValue(plugin, lastBadOmenValue)
                val lastBadOmenMeta: MetadataValue = FixedMetadataValue(plugin, 19)
                player.setMetadata("lastBadOmenValue", lastBadOmenMeta)
                val badOmenEffect = PotionEffect(PotionEffectType.BAD_OMEN, duration, 19)
                player.addPotionEffect(badOmenEffect)
            }
        }
    }

    private fun spawnCustomWave(currentWave: Int, loc: Location, logger: Logger) {

        val randomVector = Vector.getRandom()
        randomVector.x -= 0.5;
        randomVector.z -= 0.5;
        val spawnLoc: Location = loc.add(randomVector.multiply(50))
        val world: World = loc.world

        val illagerMap: Map<EntityType, Double> = mapOf(
            Pair(EntityType.PILLAGER, 0.7),
            Pair(EntityType.VINDICATOR, 0.6),
            Pair(EntityType.ILLUSIONER, 0.4),
            Pair(EntityType.WITCH, 0.4),
            Pair(EntityType.RAVAGER, 0.4),
            Pair(EntityType.EVOKER, 0.3)
        )
        illagerMap.forEach { illagerPair->
            val count: Int = (illagerPair.value * currentWave).roundToInt()
            logger.info("Spawning " + count + " of " + illagerPair.key.name + " at " + spawnLoc.toString())
            for (i in 1..count) {
                world.spawnEntity(spawnLoc, illagerPair.key) as Raider
            }
        }
    }
}