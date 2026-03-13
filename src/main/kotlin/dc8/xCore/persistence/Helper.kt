package dc8.xCore.persistence

import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

inline fun DataSource.executeParameterizedCommand(sql: String, block: PreparedStatement.() -> Unit): Int {
    connection.use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            stmt.block()
            return stmt.executeUpdate()
        }
    }
}

inline fun <T> DataSource.executeParameterizedQuery(
    sql: String,
    bind: PreparedStatement.() -> Unit,
    map: ResultSet.() -> T
): T {
    connection.use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            stmt.bind()
            stmt.executeQuery().use { return it.map() }
        }
    }
}