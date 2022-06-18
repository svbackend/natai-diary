package db.migration

import com.svbackend.natai.entity.*
import org.flywaydb.core.api.migration.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

class V3__create_tags : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(Tags)
        }
    }
}