package dc8.xCore.commands.management

import dc8.xCore.XCore
import dc8.xCore.globals.PLUGIN_NAME
import dc8.xCore.globals.Permissions
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

class ReloadCmd(private val xCore: XCore) : BasicCommand {
    override fun execute(context : CommandSourceStack, rawArgs : Array<out String>) {
        if (rawArgs.isEmpty()) {
            context.sender.sendMessage(
                text("$PLUGIN_NAME is running :)", NamedTextColor.GREEN)
            )
        }

        when(rawArgs[0]) {
            "reload" -> reload(context)
            else -> context.sender.sendMessage(
                text("Invalid command '${rawArgs[0]}'")
            )
        }
    }

    override fun permission(): String = Permissions.Reload.RELOAD

    private fun reload(context: CommandSourceStack) {
        context.sender.sendMessage(
            text("Reloading $PLUGIN_NAME config...", NamedTextColor.GRAY)
        )
        xCore.reloadConfig()
        context.sender.sendMessage(
            text("Reloaded $PLUGIN_NAME config.", NamedTextColor.GREEN)
        )
    }
}