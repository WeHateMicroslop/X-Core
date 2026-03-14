package dc8.xCore.persistence

data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String,
    val maxPoolSize: Int,
    val minIdle: Int,
    val connectionTimeout: Long
)