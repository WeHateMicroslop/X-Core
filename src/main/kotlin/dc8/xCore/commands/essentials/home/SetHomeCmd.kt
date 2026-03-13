package dc8.xCore.commands.essentials.home

import dc8.xCore.XCore
import dc8.xCore.commands.append
import dc8.xCore.commands.failUnit
import dc8.xCore.globals.ConfigKeys
import dc8.xCore.globals.Permissions
import dc8.xCore.globals.runAsync
import dc8.xCore.repositories.DEFAULT_HOME_NAME
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class SetHomeCmd(val xCore: XCore) : BasicCommand {
    private val homeRepository = xCore.pluginContext.homeRepository
    private val homeCache = xCore.pluginContext.homeCache

    override fun execute(context: CommandSourceStack, rawArgs: Array<out String>) {
        val sender = context.sender

        if (sender !is Player) {
            return sender.failUnit(Responses.invalidSender)
        }

        val canBypassLimit = sender.hasPermission(Permissions.Home.BYPASS_LIMIT)
        val homeName = if (rawArgs.isEmpty()) null else rawArgs[0]

        xCore.runAsync {
            var response: Component? = null
            val limit = xCore.config.getInt(ConfigKeys.Home.LIMIT, 3)
            if (
                !canBypassLimit
                && homeRepository.countHomes(sender) >= limit
                ) {
                response = Responses.homeLimitReached(limit)
            }

            if (response == null && !homeRepository.tryCreateHome(sender, homeName)) {
                response = Responses.homeAlreadyExists(homeName ?: DEFAULT_HOME_NAME)
            }

            if (response == null) {
                homeCache.addToCache(sender, homeName ?: DEFAULT_HOME_NAME)
                response = Responses.success(homeName ?: DEFAULT_HOME_NAME)
            }

            sender.scheduler.run(
                xCore,
                { _ ->
                    sender.sendMessage(response)
                },
                null
            )
        }
    }

    override fun permission(): String = Permissions.Home.SET

    /**
     * Predefined error messages used by the command.
     */
    private object Responses {
        /**
         * The sender is invalid.
         */
        val invalidSender = text(
            "You need to be a player to use this command.",
            NamedTextColor.RED
        )

        /**
         * The command can not be executed because of a permission error.
         */
        fun homeLimitReached(homeLimit: Int): Component =
            text("You have already reached the limit of ", NamedTextColor.RED)
                .append(homeLimit.toString(), NamedTextColor.LIGHT_PURPLE)
                .append(" homes.", NamedTextColor.RED)

        fun homeAlreadyExists(homeName: String): Component =
            text("You already have a home with the name ", NamedTextColor.RED)
                .append(homeName, NamedTextColor.LIGHT_PURPLE)
                .append(".", NamedTextColor.RED)

        fun success(homeName: String): Component =
            text("Created your home ", NamedTextColor.GREEN)
                .append(homeName, NamedTextColor.LIGHT_PURPLE)
                .append(".", NamedTextColor.GREEN)
    }
}