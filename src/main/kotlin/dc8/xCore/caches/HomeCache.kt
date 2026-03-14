package dc8.xCore.caches

import dc8.xCore.globals.runAsync
import dc8.xCore.repositories.HomeRepository
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class HomeCache(private val xCore: JavaPlugin, private val homeRepository: HomeRepository) : Listener {
    private val homeCache: ConcurrentHashMap<UUID, MutableSet<String>> = ConcurrentHashMap()

    init {
        val players = Bukkit.getOnlinePlayers()
        xCore.runAsync {
            players.forEach {
                loadPlayerHomesIntoCache(it)
            }
        }
    }

    fun addToCache(player: Player, homeName: String) {
        homeCache[player.uniqueId] = getCachedValuesOrEmptySet(player).apply {
            add(homeName)
        }
    }

    fun removeFromCache(player: Player, homeName: String? = null) {
        if (homeName == null) {
            homeCache.remove(player.uniqueId)
            return
        }

        homeCache[player.uniqueId] = getCachedValuesOrEmptySet(player).apply {
            remove(homeName)
        }
    }

    fun getCachedValuesOrEmptySet(player: Player): MutableSet<String> {
        if (!homeCache.containsKey(player.uniqueId)) {
            return mutableSetOf()
        }

        return homeCache[player.uniqueId]!!
    }

    fun clear() = homeCache.clear()

    @EventHandler
    fun loadIntoCacheOnJoin(event: PlayerJoinEvent) =
        xCore.runAsync {
            loadPlayerHomesIntoCache(event.player)
        }

    @EventHandler
    fun removeFromCacheOnLeave(event: PlayerQuitEvent) =
        homeCache.remove(event.player.uniqueId)


    private fun loadPlayerHomesIntoCache(player: Player) {
        homeCache[player.uniqueId] = homeRepository.getAllHomeNames(player).toMutableSet()
    }
}