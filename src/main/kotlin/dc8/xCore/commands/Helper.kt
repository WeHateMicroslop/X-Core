package dc8.xCore.commands

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Suggests players from the list of the online players.
 * @param currentInput filters out the players not prefixed with this input.
 * @param sender matches only the players visible to the sender. Matches all if `null`
 * @return the list of the matched player names.
 */
fun suggestPlayers(currentInput: String, sender: CommandSender? = null): List<String> =
    when (sender) {
        is Player -> Bukkit.getOnlinePlayers().filter { sender.canSee(it) }
        else -> Bukkit.getOnlinePlayers()
    }
        .map { it.name }
        .filter { it.startsWith(currentInput, true) }


/**
 * Appends without explicitly having to create a [TextComponent] every time.
 */
fun Component.append(
    text: String,
    color: NamedTextColor = NamedTextColor.GRAY,
    vararg textDecoration: TextDecoration
): Component = this.append(Component.text(text, color, *textDecoration))

/**
 * Appends without explicitly having to create a [TextComponent] every time.
 * Also appends a newLine.
 *
 * This is effectively the same as [myComponent.append(...)][Component.append].[newLine()][Component.appendNewline]
 */
fun Component.appendLine(
    text: String,
    color: NamedTextColor = NamedTextColor.GRAY,
    vararg textDecoration: TextDecoration
): Component = this.append(Component.text(text, color, *textDecoration)).appendNewline()

/**
 * Sends the given Component as a message.
 * Usually used to shorten repetitive sending of
 * error messages and returning `null` right after.
 *
 * @param message the Component to send.
 * @return `null`
 */
fun Audience.fail(message: Component): Nothing? {
    sendMessage(message)
    return null
}
/**
 * Sends the given Component as a message.
 * Usually used to shorten repetitive sending of
 * error messages and returning [Unit] right after.
 *
 * @param message the Component to send.
 * @return [Unit]
 */
fun Audience.failUnit(message: Component) {
    sendMessage(message)
}
