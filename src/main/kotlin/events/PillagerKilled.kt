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
import org.jetbrains.annotations.Nullable
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

class PillagerKilled(private val plugin: Plugin, private val maxAmp: Int, private val duration: Int) : Listener {

    @EventHandler
    fun entityKill(event: EntityDeathEvent) {
        val entity: LivingEntity = event.entity
        if (entity is Raider) {
            entity.world.raids.forEach { raid ->
                if (entity.location.distance(raid.location) < 200) {
                    val raidChunk: Chunk = raid.location.chunk
                    val currentWaveKey = NamespacedKey(plugin, "currentWave")
                    val wavesOverrideKey = NamespacedKey(plugin, "wavesOverride")
                    val currentWave: Int? = raidChunk.persistentDataContainer.get(currentWaveKey, PersistentDataType.INTEGER)
                    val wavesOverride: Int? = raidChunk.persistentDataContainer.get(wavesOverrideKey, PersistentDataType.INTEGER)
                    if (currentWave != null && wavesOverride != null) {
                        val raiders: List<Raider?> = raid.raiders
                        if (currentWave > raid.totalWaves && currentWave < wavesOverride && raiders.count() < 2) {
                            raidChunk.persistentDataContainer.set(currentWaveKey, PersistentDataType.INTEGER, currentWave + 1)
                            spawnCustomWave(currentWave, raid.location)
                            entity.world.players.forEach { player ->
                                if (player.location.distance(raid.location) < 200) {
                                    player.sendTitle("Wave " + (currentWave + 1), "", 20, 100, 20)
                                }
                            }
                        }
                    }
                    return
                }
            }
        }
        val hasBannerKey = NamespacedKey(plugin, "hasBanner")
        val player: @Nullable Player = entity.killer ?: return
        if (entity.persistentDataContainer.has(hasBannerKey, PersistentDataType.SHORT)) {
            val lastBadOmenValueKey = NamespacedKey(plugin, "lastBadOmenValue")
            var lastBadOmenValue = player.persistentDataContainer.get(lastBadOmenValueKey, PersistentDataType.INTEGER) ?: 0
            if (lastBadOmenValue < maxAmp) {
                lastBadOmenValue++
            }
            player.persistentDataContainer.set(lastBadOmenValueKey, PersistentDataType.INTEGER, lastBadOmenValue)
            val badOmenEffect = PotionEffect(PotionEffectType.BAD_OMEN, duration, lastBadOmenValue - 1)
            player.addPotionEffect(badOmenEffect)
        }
    }

    private fun spawnCustomWave(currentWave: Int, loc: Location) {
        val illagerMap: Map<EntityType, Double> = mapOf(
            Pair(EntityType.PILLAGER, 0.4),
            Pair(EntityType.VINDICATOR, 0.35),
            Pair(EntityType.RAVAGER, 0.3),
            Pair(EntityType.ILLUSIONER, 0.2),
            Pair(EntityType.WITCH, 0.15),
            Pair(EntityType.EVOKER, 0.15)
        )
        illagerMap.forEach { illagerPair->
            val count: Int = (illagerPair.value * currentWave - 0.5).roundToInt()
            for (i in 1..count) {
                val spawnLoc: Location = randomLoc(loc)
                spawnLoc.world.spawnEntity(spawnLoc, illagerPair.key) as Raider
            }
        }
    }

    private fun randomLoc(loc: Location):Location {
        val radius = 100.0
        var x: Double = (Math.random() - 0.5) * radius * 2
        var z: Double = sqrt(radius.pow(2) - x.pow(2)) * (Math.random() - 0.5).sign
        var spawnLoc: Location = loc.clone().add(x, 0.0, z)
        spawnLoc.y = spawnLoc.world.getHighestBlockYAt(spawnLoc).toDouble()
        while (spawnLoc.block.isLiquid) {
            x = (Math.random() - 0.5) * radius * 2
            z = sqrt(radius.pow(2) - x.pow(2)) * (Math.random() - 0.5).sign
            spawnLoc = loc.clone().add(x, 0.0, z)
            spawnLoc.y = spawnLoc.world.getHighestBlockYAt(spawnLoc).toDouble()
        }

        spawnLoc.y += 1.0

        return spawnLoc
    }
}