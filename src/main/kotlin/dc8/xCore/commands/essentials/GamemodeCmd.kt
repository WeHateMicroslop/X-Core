package dc8.xCore.commands.essentials

import dc8.xCore.XCore
import dc8.xCore.commands.append
import dc8.xCore.commands.appendLine
import dc8.xCore.commands.fail
import dc8.xCore.commands.suggestPlayers
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.logging.Level
import dc8.xCore.globals.Permissions.GM as Perms

/**
 * Represents the structured and validated parameters of the command.
 * @property gameMode the gamemode to change to
 * @property target the target player
 */
private data class GamemodeArgs(val gameMode: GameMode, val target: Player)

class GamemodeCmd(private val xCore: XCore) : BasicCommand {
    override fun execute(context: CommandSourceStack, rawArgs: Array<out String>) {
        val args = validate(context, rawArgs) ?: return

        if (args.target.gameMode == args.gameMode)
            return

        args.target.gameMode = args.gameMode

        args.target.sendMessage(Responses.success(args.gameMode.name.lowercase()))

        xCore.logger.log(
            Level.FINER,
            "${context.sender} changed gamemode of ${args.target.name} to ${args.gameMode.name.lowercase()}."
        )
    }

    override fun canUse(sender: CommandSender): Boolean =
        listOf(
            Perms.SURVIVAL,
            Perms.CREATIVE,
            Perms.ADVENTURE,
            Perms.SPECTATOR
        ).any {
            sender.hasPermission(it)
        }

    override fun suggest(context: CommandSourceStack, args: Array<out String>): List<String> {
        return when {
            args.size <= 1 -> getPermittedGamemodes(context.sender)
            args.size == 2 && context.sender.hasPermission(Perms.OTHERS) ->
                suggestPlayers(args[1], context.sender)

            else -> emptyList()
        }
    }

    /**
     * Checks the permissions of the given [CommandSender] and builds a list of valid input
     * suggestions as the gamemode parameter.
     * @param sender the command sender.
     * @return a list of valid [String] inputs based on permission limitations.
     */
    private fun getPermittedGamemodes(sender: CommandSender): List<String> {
        return buildList(4) {
            if (sender.hasPermission(Perms.SURVIVAL)) add("0")
            if (sender.hasPermission(Perms.CREATIVE)) add("1")
            if (sender.hasPermission(Perms.ADVENTURE)) add("2")
            if (sender.hasPermission(Perms.SPECTATOR)) add("3")
        }
    }

    /**
     * Validates command arguments and maps them to [GamemodeArgs].
     *
     * @param context the command source context
     * @param rawArgs the raw arguments passed to the command
     * @return the parsed [GamemodeArgs], or `null` if validation fails
     */
    private fun validate(context: CommandSourceStack, rawArgs: Array<out String>): GamemodeArgs? {
        val sender = context.sender

        // valid number of args and valid gamemode?
        val gamemodeInt = rawArgs.firstOrNull()?.toIntOrNull()
        if (gamemodeInt == null || gamemodeInt !in 0..3)
            return sender.fail(Responses.invalidSyntax)

        // is the target valid?
        if (rawArgs.size == 1 && context.executor !is Player)
            return sender.fail(Responses.invalidTarget)
        if (rawArgs.size == 2) {
            if (!sender.hasPermission(Perms.OTHERS))
                return sender.fail(Responses.permissionError)

            if (Bukkit.getPlayer(rawArgs[1]) == null)
                return sender.fail(Responses.invalidTarget)
        }

        // has permission for this gamemode?
        val gamemode = mapGamemode(gamemodeInt)
        if (!sender.hasPermission(Perms.BASE_MODE + gamemodeInt))
            return sender.fail(Responses.permissionError)

        val targetPlayer = when(rawArgs.size) {
            2 -> Bukkit.getPlayer(rawArgs[1])!!
            else -> context.executor as Player
        }

        return GamemodeArgs(gamemode, targetPlayer)
    }

    /**
     * Maps from an [Int] to a [GameMode].
     * @param gamemodeId the ID of the gamemode.
     * @return the respective [GameMode].
     * @throws IllegalArgumentException when the ID does not match a [GameMode].
     */
    private fun mapGamemode(gamemodeId: Int): GameMode {
        return when (gamemodeId) {
            0 -> GameMode.SURVIVAL
            1 -> GameMode.CREATIVE
            2 -> GameMode.ADVENTURE
            3 -> GameMode.SPECTATOR
            else -> throw IllegalArgumentException() // Should never happen
        }
    }
}

/**
 * Predefined error messages used by the command.
 */
private object Responses {
    fun success(gamemode: String): Component =
        text("Gamemode changed to ", NamedTextColor.GREEN)
            .append(gamemode, NamedTextColor.LIGHT_PURPLE)

    /**
     * The target is invalid.
     */
    val invalidTarget = text(
        "The target is not a valid player.",
        NamedTextColor.RED
    )

    /**
     * The command has a syntax error.
     */
    val invalidSyntax = text(
        "Invalid syntax. Expected syntax:",
        NamedTextColor.RED
    )
        .appendNewline()
        .append("/gm ", NamedTextColor.WHITE)
        .append(
            text("<0|1|2|3>", NamedTextColor.LIGHT_PURPLE, TextDecoration.UNDERLINED)
                .hoverEvent(
                    text("Gamemode ID", NamedTextColor.LIGHT_PURPLE).appendNewline()
                        .appendLine("0 → survival", NamedTextColor.GRAY)
                        .appendLine("1 → creative", NamedTextColor.GRAY)
                        .appendLine("2 → adventure", NamedTextColor.GRAY)
                        .append("3 → spectator", NamedTextColor.GRAY)
                )
        )
        .appendSpace()
        .append(
            text("[player]", NamedTextColor.LIGHT_PURPLE, TextDecoration.UNDERLINED)
                .hoverEvent(
                    HoverEvent.showText(
                        text("Optional target player", NamedTextColor.GRAY)
                    )
                )
        )

    /**
     * The command can not be executed because of a permission error.
     */
    val permissionError = text(
        "You don't have permission to use this command (like this).", NamedTextColor.RED
    )
}