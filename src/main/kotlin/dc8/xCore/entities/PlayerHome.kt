package dc8.xCore.entities

import org.bukkit.Location

data class PlayerHome(
    val uuid: String,
    val name: String,
    val worldName: String,
    val location: Location
)