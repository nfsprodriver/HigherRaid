package events

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TranslatableComponent
import org.bukkit.*
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class EntitySpawn(private val plugin: Plugin, private val addProb: Double) : Listener {

    @EventHandler
    fun entitySpawn(event: EntitySpawnEvent) {
        if (event.entity is LivingEntity) {
            val entity: LivingEntity = event.entity as LivingEntity
            if (entity is Pillager || entity.type == EntityType.PILLAGER) { //same
                val random: Double = Math.random()
                val captain: Boolean = random < plugin.config.getDouble("addProb")
                if (captain && entity.equipment?.helmet?.type != Material.WHITE_BANNER) {
                    entity.equipment?.helmet = getOminousBanner(1)
                    entity.equipment?.helmetDropChance = 1.0F
                }
            }
            if (entity.equipment?.helmet?.type == Material.WHITE_BANNER) {
                val hasBannerKey = NamespacedKey(plugin, "hasBanner")
                entity.persistentDataContainer.set(hasBannerKey, PersistentDataType.SHORT, 1)
            }
        }
    }

    private fun getOminousBanner(amount: Int): ItemStack {
        val ominousBanner = ItemStack(Material.WHITE_BANNER, amount)
        val meta: BannerMeta = ominousBanner.itemMeta as BannerMeta
        meta.addPattern(Pattern(DyeColor.CYAN, PatternType.RHOMBUS_MIDDLE))
        meta.addPattern(Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM))
        meta.addPattern(Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER))
        meta.addPattern(Pattern(DyeColor.LIGHT_GRAY, PatternType.BORDER))
        meta.addPattern(Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE))
        meta.addPattern(Pattern(DyeColor.LIGHT_GRAY, PatternType.HALF_HORIZONTAL))
        meta.addPattern(Pattern(DyeColor.LIGHT_GRAY, PatternType.CIRCLE_MIDDLE))
        meta.addPattern(Pattern(DyeColor.BLACK, PatternType.BORDER))
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
        val displayName = TranslatableComponent()
        displayName.color = ChatColor.GOLD
        displayName.translate = "block.minecraft.ominous_banner"
        meta.setDisplayNameComponent(arrayOf(displayName))
        ominousBanner.itemMeta = meta

        return ominousBanner
    }
}