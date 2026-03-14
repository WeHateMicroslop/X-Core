package dc8.xCore.commands.essentials.home

import dc8.xCore.XCore
import dc8.xCore.commands.append
import dc8.xCore.commands.failUnit
import dc8.xCore.globals.ConfigKeys
import dc8.xCore.globals.Permissions
import dc8.xCore.globals.runAsync
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class HomesCmd(val xCore: XCore) : BasicCommand {
    private val homeRepository = xCore.pluginContext.homeRepository
    override fun execute(context: CommandSourceStack, rawArgs: Array<out String>) {
        val sender = context.sender

        if (sender !is Player) {
            return sender.failUnit(Responses.invalidSender)
        }

        xCore.runAsync {
            val homeNames = homeRepository.getAllHomeNames(sender)

            sender.scheduler.run(
                xCore,
                { _ -> listHomes(sender, homeNames) }, null
            )
        }
    }

    override fun permission(): String = Permissions.Home.LIST

    private fun listHomes(sender: Player, homeNames: List<String>) {
        if (homeNames.isEmpty()) {
            sender.sendMessage(Responses.noHomesFound)
            return
        }

        var response = text("Homes", NamedTextColor.LIGHT_PURPLE)
            .append(":")
            .appendSpace()
            .append(
                if (sender.hasPermission(Permissions.Home.BYPASS_LIMIT))
                    "(${homeNames.size})"
                else "(${homeNames.size}/${xCore.config.getInt(ConfigKeys.Home.LIMIT, 3)})",
                NamedTextColor.DARK_GRAY
            )
        homeNames.forEach {
            response = response.appendNewline().append(" - $it")
        }

        sender.sendMessage(response)
    }

    /**
     * Error message templates used by the command.
     */
    private object Responses {
        val invalidSender = text(
            " You need to be a player to use this command.",
            NamedTextColor.RED
        )

        val noHomesFound = text(
            "You don't have any homes.",
            NamedTextColor.RED
        )
    }
}