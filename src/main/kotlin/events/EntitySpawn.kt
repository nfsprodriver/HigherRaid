package events

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

class EntitySpawn(private val plugin: Plugin, private val logger: Logger) : Listener {

    @EventHandler
    fun entitySpawn(event: EntitySpawnEvent) {
        if (event.entity is LivingEntity) {
            val entity: LivingEntity = event.entity as LivingEntity
            if (entity.equipment?.helmet?.type == Material.WHITE_BANNER) {
                val hasBannerKey = NamespacedKey(plugin, "hasBanner")
                entity.persistentDataContainer.set(hasBannerKey, PersistentDataType.SHORT, 1)
            }
        }
    }
}