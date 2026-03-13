package dc8.xCore.globals

import org.bukkit.plugin.java.JavaPlugin

const val PLUGIN_NAME = "X-Core"

fun JavaPlugin.runAsync(block: () -> Unit) {
    server.asyncScheduler.runNow(this) { _ ->
        block()
    }
}