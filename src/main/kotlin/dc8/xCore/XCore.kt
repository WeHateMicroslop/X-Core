package dc8.xCore

import dc8.xCore.commands.management.ReloadCmd
import dc8.xCore.commands.essentials.GamemodeCmd
import dc8.xCore.commands.essentials.home.*
import dc8.xCore.globals.PLUGIN_NAME
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class XCore : JavaPlugin() {
    lateinit var pluginContext: PluginContext
    override fun onEnable() {
        saveDefaultConfig()

        pluginContext = PluginContext(this)

        registerCommands()

        logger.log(Level.INFO, "Enabled $PLUGIN_NAME.")
    }


    override fun onDisable() {
        pluginContext.cleanUp()
        logger.log(Level.INFO, "Disabled $PLUGIN_NAME.")
    }

    private fun registerCommands() {
        // Management

        this.registerCommand(
            "xcore",
            "Manage $PLUGIN_NAME.",
            ReloadCmd(this)
        )
        // Essentials
        this.registerCommand(
            "gm",
            "Shorthand for the normal /gamemode command.",
            GamemodeCmd(this)
        )
        this.registerCommand(
            "home",
            "Teleport to your home.",
            HomeCmd(this)
        )
        this.registerCommand(
            "sethome",
            "Create your home.",
            SetHomeCmd(this)
        )
        this.registerCommand(
            "deletehome",
            "Delete your home.",
            listOf("delhome", "removehome", "rmhome"),
            DeleteHomeCmd(this)
        )
        this.registerCommand(
            "homes",
            "Lists your homes.",
            HomesCmd(this)
        )
        this.registerCommand(
            "locatehome",
            "Locate your homes.",
            listOf("lhome"),
            LocateHomeCmd(this)
        )
    }
}
