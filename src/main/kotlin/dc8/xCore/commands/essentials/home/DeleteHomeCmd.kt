package dc8.xCore.commands.essentials.home

import dc8.xCore.XCore
import dc8.xCore.commands.append
import dc8.xCore.commands.failUnit
import dc8.xCore.globals.Permissions
import dc8.xCore.globals.runAsync
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class DeleteHomeCmd(val xCore: XCore) : BasicCommand {
    private val homeRepository = xCore.pluginContext.homeRepository
    private val homeCache = xCore.pluginContext.homeCache
    override fun execute(context: CommandSourceStack, rawArgs: Array<out String>) {
        val sender = context.sender

        if (sender !is Player) {
            return sender.failUnit(Responses.invalidSender)
        }

        if (rawArgs.isEmpty()) {
            return sender.failUnit(Responses.invalidSyntax)
        }

        val homeName = rawArgs[0]

        xCore.runAsync {
            if (!homeRepository.deleteHome(sender, homeName)) {
                sender.scheduler.run(
                    xCore,
                    { _ -> sender.failUnit(Responses.homeNotFound(homeName)) },
                    null
                )
                return@runAsync
            }

            sender.scheduler.run(
                xCore,
                { _ -> success(sender, homeName) }, null
            )
        }
    }

    override fun suggest(context: CommandSourceStack, args: Array<out String>): MutableCollection<String> {
        return when (args.size) {
            0 -> homeCache.getCachedValuesOrEmptySet(context.sender as Player)
            1 -> homeCache.getCachedValuesOrEmptySet(context.sender as Player).filter {
                it.startsWith(args[0])
            }.toMutableList()

            else -> mutableListOf()
        }
    }

    override fun permission(): String = Permissions.Home.DELETE

    private fun success(sender: Player, homeName: String) {
        xCore.pluginContext.homeCache.removeFromCache(sender, homeName)
        sender.sendMessage(Responses.success(homeName))
    }


    /**
     * Error message templates used by the command.
     */
    private object Responses {
        val invalidSender = text(
            " You need to be a player to use this command.",
            NamedTextColor.RED
        )

        val invalidSyntax =
            text(
                "Invalid syntax. Expected syntax:",
                NamedTextColor.RED
            ).appendNewline()
                .append("/deletehome ", NamedTextColor.WHITE)
                .append(text("<home_name>", NamedTextColor.LIGHT_PURPLE))

        fun homeNotFound(homeName: String): Component =
            text("You don't have a home with the name ", NamedTextColor.RED)
                .append(homeName, NamedTextColor.LIGHT_PURPLE)
                .append(".", NamedTextColor.RED)

        fun success(homeName: String): Component =
            text("Deleted your home ", NamedTextColor.GREEN)
                .append(homeName, NamedTextColor.LIGHT_PURPLE)
                .append(".", NamedTextColor.GREEN)
    }
}