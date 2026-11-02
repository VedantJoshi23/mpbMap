package io.github.vedantjoshi23.kairos.feature.location.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentApi
import io.github.vedantjoshi23.kairos.feature.location.api.ConsentKind
import javax.inject.Inject
import javax.inject.Singleton

/** ConsentRecord, device-only, every kind with withdrawal (Law 12; DISC-DATA-MODEL v1.1.0). */
@Entity(tableName = "consent_record")
data class ConsentRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kind: String,
    val shownAtMs: Long,
    val granted: Boolean,
    val withdrawnAtMs: Long?,
    val appVersion: String,
)

@Dao
interface ConsentDao {
    @Insert suspend fun insert(record: ConsentRecordEntity)
    @Query("SELECT * FROM consent_record WHERE kind = :kind ORDER BY shownAtMs DESC, id DESC LIMIT 1")
    suspend fun latest(kind: String): ConsentRecordEntity?
    @Query("UPDATE consent_record SET withdrawnAtMs = :at WHERE kind = :kind AND withdrawnAtMs IS NULL")
    suspend fun withdraw(kind: String, at: Long)
}

@Database(entities = [ConsentRecordEntity::class], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun consent(): ConsentDao
}

@Singleton
class ConsentStore @Inject constructor(@ApplicationContext context: Context) : ConsentApi {
    private val db by lazy {
        Room.databaseBuilder(context.applicationContext, LocationDatabase::class.java, "location.db").build()
    }
    private val version = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
    }.getOrDefault("?")

    override suspend fun record(kind: ConsentKind, granted: Boolean) {
        db.consent().insert(ConsentRecordEntity(kind = kind.name, shownAtMs = System.currentTimeMillis(), granted = granted, withdrawnAtMs = null, appVersion = version))
    }

    override suspend fun withdraw(kind: ConsentKind) {
        db.consent().withdraw(kind.name, System.currentTimeMillis())
    }

    override suspend fun isInForce(kind: ConsentKind): Boolean =
        db.consent().latest(kind.name)?.let { it.granted && it.withdrawnAtMs == null } ?: false
}
