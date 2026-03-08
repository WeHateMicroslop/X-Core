package dc8.xCore.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import net.kyori.adventure.text.TextComponent

object Helper {
    /**
     * Suggests players from the list of the online players.
     * @param currentInput filters out the players not prefixed with this input.
     * @param sender matches only the players visible to the sender. Matches all if `null`
     * @return the list of the matched player names.
     */
    fun suggestPlayers(currentInput: String, sender: CommandSender? = null): List<String> {
        return (
                if (sender is Player)
                    Bukkit.getOnlinePlayers().filter { sender.canSee(it) }
                else Bukkit.getOnlinePlayers()
                )
            .map { it.name }
            .filter { it.startsWith(currentInput, true) }
    }

    /**
     * Appends without explicitly having to create a [TextComponent] every time.
     */
    fun Component.append(
        text: String,
        color: NamedTextColor = NamedTextColor.WHITE,
        vararg textDecoration: TextDecoration): Component {
        return this.append(Component.text(text, color, *textDecoration))
    }

    /**
     * Appends without explicitly having to create a [TextComponent] every time.
     * Also appends a newLine.
     *
     * This is effectively the same as [myComponent.append(...)][Component.append].[newLine()][Component.appendNewline]
     */
    fun Component.appendLine(
        text: String,
        color: NamedTextColor = NamedTextColor.WHITE,
        vararg textDecoration: TextDecoration): Component {
        return this.append(Component.text(text, color, *textDecoration)).appendNewline()
    }

    /**
     * Checks if this [CommandSender] has any of the provided permissions.
     *
     * @param permissions the list of permissions to check against.
     * @return `true` if there was a match, otherwise `false`.
     */
    fun CommandSender.hasAnyPermissionOf(vararg permissions: String): Boolean {
        for(permission in permissions) {
            if (this.hasPermission(permission))
                return true
        }

        return false
    }
}