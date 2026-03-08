package dc8.xCore.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Helper {
    fun suggestPlayers(currentInput: String, sender: CommandSender? = null): List<String> {
        return (
                if (sender is Player)
                    Bukkit.getOnlinePlayers().filter { sender.canSee(it) }
                else Bukkit.getOnlinePlayers()
                )
            .map { it.name }
            .filter { it.startsWith(currentInput, true) }
    }

    fun Component.appendLine(component: Component): Component {
        return this.append(component).appendNewline()
    }

    fun Component.append(
        text: String,
        color: NamedTextColor = NamedTextColor.WHITE,
        vararg textDecoration: TextDecoration): Component {
        return this.append(Component.text(text, color, *textDecoration))
    }

    fun Component.appendLine(
        text: String,
        color: NamedTextColor = NamedTextColor.WHITE,
        vararg textDecoration: TextDecoration): Component {
        return this.appendLine(Component.text(text, color, *textDecoration))
    }

    fun CommandSender.hasAnyPermission(vararg permissions: String): Boolean {
        for(permission in permissions) {
            if (this.hasPermission(permission))
                return true
        }

        return false
    }
}