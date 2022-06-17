package db.migration

import com.svbackend.natai.entity.*
import org.flywaydb.core.api.migration.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

class V2__add_actualDate_to_notes : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Notes)
            exec("""UPDATE notes SET actual_date = date(created_at);""")
        }
    }
}