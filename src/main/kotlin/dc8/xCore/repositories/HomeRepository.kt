package dc8.xCore.repositories

import dc8.xCore.entities.PlayerHome
import dc8.xCore.persistence.executeParameterizedCommand
import dc8.xCore.persistence.executeParameterizedQuery
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

const val DEFAULT_HOME_NAME = "home"
class HomeRepository(private val dataSource: DataSource) {
    init {
        val sql = """
            CREATE TABLE IF NOT EXISTS player_home (
                uuid TEXT,
                name TEXT,
                world_name TEXT NOT NULL,
                x DOUBLE NOT NULL,
                y DOUBLE NOT NULL,
                z DOUBLE NOT NULL,
                rot_yaw FLOAT NOT NULL,
                rot_pitch FLOAT NOT NULL,
                PRIMARY KEY (uuid, name)
            );
        """.trimIndent()

        dataSource.connection.createStatement().execute(sql)
    }


    fun countHomes(player: Player): Int {
        val sql = """
            SELECT COUNT(1)
            FROM player_homes
            WHERE uuid = ?
        """.trimIndent()

        return dataSource.executeParameterizedQuery(sql,
            bind = {
                setString(1, player.uniqueId.toString())
            }, map = {
                getInt(1)
            })
    }

    fun tryCreateHome(player: Player, homeName: String?): Boolean {
        val sql = "INSERT INTO player_homes VALUES(?, ?, ?, ?, ?, ?, ?, ?)"

        return try {
            dataSource.executeParameterizedCommand(sql) {
                setString(1, player.uniqueId.toString())
                setString(2, homeName ?: DEFAULT_HOME_NAME)
                setString(3, player.world.name)
                setDouble(4, player.location.x)
                setDouble(5, player.location.y)
                setDouble(6, player.location.z)
                setFloat(7, player.location.yaw)
                setFloat(8, player.location.pitch)
            } > 0
        } catch (exception: SQLException) {
            false
        }
    }

    fun getAllHomes(player: Player): List<PlayerHome> {
        val sql = """
            SELECT *
            FROM player_homes
            WHERE uuid = ?
        """.trimIndent()

        return dataSource.executeParameterizedQuery(
            sql,
            bind = {
                setString(1, player.uniqueId.toString())
            },
            map = { mapRows { toPlayerHome() } }
        )
    }

    fun getAllHomeNames(player: Player): List<String> {
        val sql = """
            SELECT name
            FROM player_homes
            WHERE uuid = ?
        """.trimIndent()

        return dataSource.executeParameterizedQuery(
            sql,
            bind = {
                setString(1, player.uniqueId.toString())
            },
            map = { mapRows { getString(1) } }
        )
    }

    fun tryGetHome(player: Player, homeName: String?): PlayerHome? {
        val sql = """
            SELECT *
            FROM player_homes
            WHERE uuid = ?
            AND name = ? 
        """.trimIndent()

        return dataSource.executeParameterizedQuery(
            sql,
            bind = {
                setString(1, player.uniqueId.toString())
                setString(2, homeName ?: DEFAULT_HOME_NAME)
            },
            map = { if (next()) toPlayerHome() else null }
        )
    }

    fun deleteHome(player: Player, homeName: String): Boolean {
        val sql = """
            DELETE FROM player_homes
            WHERE uuid = ?
            AND name = ?
        """.trimIndent()

        return dataSource.executeParameterizedCommand(sql) {
            setString(1, player.uniqueId.toString())
            setString(2, homeName)
        } > 0
    }

    private fun ResultSet.toPlayerHome(): PlayerHome {
        val homeName = getString(3)
        return PlayerHome(
            getString(1),
            getString(2),
            homeName,
            Location(
                Bukkit.getWorld(homeName),
                getDouble(4),
                getDouble(5),
                getDouble(6),
                getFloat(7),
                getFloat(8)
            )
        )
    }
}