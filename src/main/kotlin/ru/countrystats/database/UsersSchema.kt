package ru.countrystats.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.countrystats.database.model.RegisterUserParams
import ru.countrystats.database.model.User
import ru.countrystats.util.hashPassword

class UserService {

    object Users : Table() {
        val id = long("id").autoIncrement()
        val email = varchar("email", 50).uniqueIndex()
        val password = text("password")
        val authToken = text("authToken")
        val name = varchar("name", 30)
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun create(user: RegisterUserParams): Long = dbQuery {

        Users.insert {
            it[email] = user.email
            it[password] = hashPassword(user.password)
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

    suspend fun update(id: Long, newUser: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[email] = newUser.email
                it[password] = hashPassword(newUser.password)
                it[name] = newUser.name
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            Users.deleteWhere { Users.id eq id }
        }
    }

    suspend fun userByEmail(email: String): User? = dbQuery {
        Users.selectAll().where { Users.email eq email }.map(::rowToUser).singleOrNull()
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private fun rowToUser(row: ResultRow) = User(
        id = row[Users.id],
        email = row[Users.email],
        password = row[Users.password],
        authToken = row[Users.authToken],
        name = row[Users.name],
        createdAt = row[Users.createdAt],
    )
}

