package dc8.xCore.commands.essentials.home

import dc8.xCore.XCore
import dc8.xCore.commands.append
import dc8.xCore.commands.fail
import dc8.xCore.commands.failUnit
import dc8.xCore.globals.Permissions
import dc8.xCore.globals.runAsync
import dc8.xCore.repositories.DEFAULT_HOME_NAME
import dc8.xCore.repositories.PlayerHome
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player

class LocateHomeCmd(val xCore: XCore) : BasicCommand {
    private val homeRepository = xCore.pluginContext.homeRepository
    private val homeCache = xCore.pluginContext.homeCache
    override fun execute(context: CommandSourceStack, rawArgs: Array<out String>) {
        val sender = context.sender

        if (sender !is Player) {
            return sender.failUnit(Responses.invalidSender)
        }

        val homeName = if (rawArgs.isEmpty()) null else rawArgs[0]

        xCore.runAsync {
            val home = homeRepository.tryGetHome(sender, homeName)

            if (home == null) {
                sender.scheduler.run(
                    xCore,
                    { _ -> sender.fail(Responses.homeNotFound(homeName ?: DEFAULT_HOME_NAME)) },
                    null
                )
                return@runAsync
            }


            sender.scheduler.run(
                xCore,
                { _ -> locateHome(sender, home) }, null
            )
        }
    }

    override fun permission(): String = Permissions.Home.LOCATE
    override fun suggest(context: CommandSourceStack, args: Array<out String>): MutableCollection<String> {
        return when (args.size) {
            0 -> homeCache.getCachedValuesOrEmptySet(context.sender as Player)
            1 -> homeCache.getCachedValuesOrEmptySet(context.sender as Player).filter {
                it.startsWith(args[0])
            }.toMutableList()

            else -> mutableListOf()
        }
    }

    private fun locateHome(sender: Player, home: PlayerHome) {
        val worldSuffix = text(" | ", NamedTextColor.GRAY).run {
            when (Bukkit.getWorld(home.worldName)?.environment) {
                World.Environment.NORMAL -> append("overworld")
                World.Environment.NETHER -> append("nether")
                World.Environment.THE_END -> append("the end")
                else -> text("")
            }
        }

        sender.sendMessage(
            text(
                home.name, NamedTextColor.LIGHT_PURPLE
            )
                .append(":")
                .append(" ${home.location.x} ${home.location.y} ${home.location.z}", NamedTextColor.WHITE)
                .append(worldSuffix)
        )
    }

    /**
     * Error message templates used by the command.
     */
    private object Responses {
        val invalidSender = text(
            "You need to be a player to use this command.",
            NamedTextColor.RED
        )

        fun homeNotFound(homeName: String): Component =
            text("You don't have a home with the name ", NamedTextColor.RED)
                .append(homeName, NamedTextColor.LIGHT_PURPLE)
                .append(".", NamedTextColor.RED)
    }
}