package events

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.persistence.PersistentDataType
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
                player.world.raids.forEach { raid ->
                    if (player.location.distance(raid.location) < 100) {
                        val raidChunk: Chunk = raid.location.chunk
                        val currentWaveKey = NamespacedKey(plugin, "currentWave")
                        val wavesOverrideKey = NamespacedKey(plugin, "wavesOverride")
                        val currentWave: Int? = raidChunk.persistentDataContainer.get(currentWaveKey, PersistentDataType.INTEGER)
                        val wavesOverride: Int? = raidChunk.persistentDataContainer.get(wavesOverrideKey, PersistentDataType.INTEGER)
                        if (currentWave != null && wavesOverride != null) {
                            val raiders: List<Raider?> = raid.raiders
                            if (currentWave > raid.totalWaves && currentWave < wavesOverride && raiders.count() < 2) {
                            //if (raiders.count() < 2) {
                                raidChunk.persistentDataContainer.set(currentWaveKey, PersistentDataType.INTEGER, currentWave + 1)
                                spawnCustomWave(currentWave + 1, raid.location, logger)
                                player.sendTitle("Wave " + (currentWave + 1).toString(), "", 20, 100, 20)
                            }
                        }
                        return
                    }
                }
                val lastBadOmenValueKey = NamespacedKey(plugin, "lastBadOmenValue")
                var lastBadOmenValue = player.persistentDataContainer.get(lastBadOmenValueKey, PersistentDataType.INTEGER) ?: 0
                if (lastBadOmenValue < maxAmp) {
                    lastBadOmenValue++
                }
                //val lastBadOmenMeta: MetadataValue = FixedMetadataValue(plugin, lastBadOmenValue)
                player.persistentDataContainer.set(lastBadOmenValueKey, PersistentDataType.INTEGER, lastBadOmenValue)
                val badOmenEffect = PotionEffect(PotionEffectType.BAD_OMEN, duration, lastBadOmenValue)
                player.addPotionEffect(badOmenEffect)
            }
        }
    }

    private fun spawnCustomWave(currentWave: Int, loc: Location, logger: Logger) {

        val randomVector = Vector.getRandom()
        randomVector.x -= 0.5;
        randomVector.z -= 0.5;
        val spawnLoc: Location = loc.add(randomVector.multiply(50)).clone()
        val world: World = loc.world
        spawnLoc.y = world.getHighestBlockYAt(spawnLoc).toDouble() + 1.0

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