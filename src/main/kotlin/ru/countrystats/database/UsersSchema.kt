package ru.countrystats.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.countrystats.database.model.RegisterUserParams
import ru.countrystats.database.model.User
import ru.countrystats.security.hashPassword

class UserService {

    object Users : Table() {
        val id = long("id").autoIncrement()
        val email = varchar("email", 50).uniqueIndex()
        val name = varchar("name", 30)
        val password = text("password")
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
        val refreshToken = text("refresh_token")

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun create(user: RegisterUserParams, token: String): Long = dbQuery {
        Users.insert {
            it[email] = user.email
            it[password] = hashPassword(user.password)
            it[refreshToken] = token
            it[name] = user.name
        }[Users.id]
    }

    suspend fun read(id: Long): User? {
        return dbQuery {
            Users.selectAll()
                .where { Users.id eq id }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    suspend fun updateRefreshToken(userEmail: String, newRefreshToken: String) {
        dbQuery {
            Users.update({ Users.email eq userEmail }) {
                it[refreshToken] = newRefreshToken
            }
        }
    }

//    suspend fun update(userEmail: String, newUserInfo: User) {
//        dbQuery {
//            Users.update({ Users.email eq userEmail }) {
//                it[email] = newUserInfo.email
//                it[password] = hashPassword(newUserInfo.password)
//                it[name] = newUserInfo.name
//            }
//        }
//    }

    suspend fun delete(id: Long) {
        dbQuery {
            Users.deleteWhere { Users.id eq id }
        }
    }

    suspend fun userByEmail(email: String): User? = dbQuery {
        Users.selectAll().where { Users.email eq email }.map(::rowToUser).singleOrNull()
    }

//    suspend fun userByRefreshToken(refreshToken: String): User? = dbQuery {
//        Users.selectAll().where { Users.refreshToken eq refreshToken }.map(::rowToUser).singleOrNull()
//    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private fun rowToUser(row: ResultRow) = User(
        id = row[Users.id],
        email = row[Users.email],
        name = row[Users.name],
        password = row[Users.password],
        createdAt = row[Users.createdAt],
        refreshToken = row[Users.refreshToken],
    )
}

