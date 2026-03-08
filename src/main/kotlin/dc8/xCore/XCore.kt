package dc8.xCore

import dc8.xCore.commands.essentials.GamemodeCmd
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class XCore : JavaPlugin() {

    override fun onEnable() {
        logger.log(Level.INFO, "Enabled X-Core.")
        this.registerCommand(
            "gm",
            "Shorthand for the normal /gamemode command.",
            GamemodeCmd(this)
        )
    }

    override fun onDisable() {
        logger.log(Level.INFO, "Disabled X-Core.")
    }
}
