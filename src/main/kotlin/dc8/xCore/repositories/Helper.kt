package dc8.xCore.repositories

import java.sql.ResultSet

inline fun <T> ResultSet.mapRows(mapper: ResultSet.() -> T): List<T> =
    buildList {
        while (next()) {
            add(mapper())
        }
    }