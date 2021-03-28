package events

import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import java.util.logging.Logger

class EntitySpawn(private val logger: Logger) : Listener {

    @EventHandler
    fun entitySpawn(event: EntitySpawnEvent) {
        if (event.entity is LivingEntity) {
            val entity: LivingEntity = event.entity as LivingEntity
            if (entity.category == EntityCategory.ILLAGER) {
                if (entity.equipment?.helmet?.type == Material.WHITE_BANNER) {
                    logger.info(entity.type.name)
                    logger.info(entity.equipment?.armorContents.contentDeepToString())
                    entity.customName = "Captain"
                }
            }
        }
    }
}