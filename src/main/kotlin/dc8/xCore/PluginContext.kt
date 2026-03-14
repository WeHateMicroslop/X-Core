package dc8.xCore

import dc8.xCore.caches.HomeCache
import dc8.xCore.persistence.DatabaseConfig
import dc8.xCore.persistence.DatabaseManager
import dc8.xCore.repositories.HomeRepository
import org.bukkit.plugin.java.JavaPlugin

class PluginContext(xCore: JavaPlugin) {
    val databaseManager: DatabaseManager
    val homeRepository: HomeRepository
    val homeCache: HomeCache

    init {
        val dbConfig = xCore.config.run {
            DatabaseConfig(
                getString("database.url") ?: throw IllegalArgumentException("Database url is missing."),
                getString("database.username") ?: throw IllegalArgumentException("Database username is missing."),
                getString("database.password") ?: throw IllegalArgumentException("Database password is missing."),
                getInt("database.pool.maximumPoolSize"),
                getInt("database.pool.minimumIdle"),
                getLong("database.pool.connectionTimeout")
            )
        }
        databaseManager = DatabaseManager(dbConfig)
        databaseManager.start()

        homeRepository = HomeRepository(databaseManager.dataSource)
        homeCache = HomeCache(xCore, homeRepository)
        xCore.server.pluginManager.registerEvents(homeCache, xCore)
    }

    fun cleanUp() {
        databaseManager.stop()
        homeCache.clear()
    }
}