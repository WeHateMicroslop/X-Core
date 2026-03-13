package dc8.xCore.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dc8.xCore.globals.PLUGIN_NAME

class DatabaseManager(private val config: DatabaseConfig) {
    lateinit var dataSource: HikariDataSource private set

    fun start() {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.url
            username = config.username
            password = config.password
            maximumPoolSize = config.maxPoolSize
            minimumIdle = config.minIdle
            connectionTimeout = config.connectionTimeout
            poolName = "$PLUGIN_NAME-HikariPool"
        }

        dataSource = HikariDataSource(hikariConfig)
    }

    fun stop() = dataSource.close()
}