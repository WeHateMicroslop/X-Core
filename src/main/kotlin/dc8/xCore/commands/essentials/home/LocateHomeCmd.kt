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
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.IEEErem
import kotlin.math.atan2


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

            val runs = AtomicInteger(0)
            val maxRuns = 10 * 20 * 4 // seconds * 20 ticks/s * periodTicks

            sender.scheduler.runAtFixedRate(
                xCore,
                {task ->
                    if (runs.incrementAndGet() > maxRuns) {
                        task.cancel()
                        return@runAtFixedRate
                    }

                    sender.sendActionBar(getVectorDescription(sender, home))
                },
                null,
                1,
                5
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

    private fun locateHome(player: Player, home: PlayerHome) {
        val worldSuffix = text(" | ", NamedTextColor.GRAY).run {
            when (Bukkit.getWorld(home.worldName)?.environment) {
                World.Environment.NORMAL -> append("overworld")
                World.Environment.NETHER -> append("nether")
                World.Environment.THE_END -> append("the end")
                else -> text("")
            }
        }

        player.sendMessage(
            text(
                home.name, NamedTextColor.LIGHT_PURPLE
            )
                .append(":")
                .append(" ${home.location.x.toInt()} ${home.location.y.toInt()} ${home.location.z.toInt()}", NamedTextColor.WHITE)
                .append(worldSuffix)
        )
    }

    private fun getVectorDescription(player: Player, home: PlayerHome): Component {
        val yDiff = when {
            home.location.y > player.location.y -> " (above you)"
            home.location.y < player.location.y -> " (below you)"
            else -> ""
        }

        val dx = home.location.x - player.location.x
        val dz = home.location.z - player.location.z

        val targetYaw = Math.toDegrees(atan2(-dx, dz))
        val angle = (targetYaw - player.location.yaw).IEEErem(360.0)

        return text(getArrowFromAngle(angle), NamedTextColor.GRAY).append(yDiff)
    }

    private fun getArrowFromAngle(angle: Double): String =
        when {
            angle >= -22.5 && angle < 22.5 -> "↑"
            angle >= 22.5 && angle < 67.5 -> "⬈"
            angle >= 67.5 && angle < 112.5 -> "→"
            angle >= 112.5 && angle < 157.5 -> "⬊"
            angle >= 157.5 || angle < -157.5 -> "↓"
            angle >= -157.5 && angle < -112.5 -> "⬋"
            angle >= -112.5 && angle < -67.5 -> "←"
            angle >= -67.5 -> "⬉"
            else -> "•"
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