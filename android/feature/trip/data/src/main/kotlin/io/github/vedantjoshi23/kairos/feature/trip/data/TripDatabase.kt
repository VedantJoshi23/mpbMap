package io.github.vedantjoshi23.kairos.feature.trip.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert

/** Trip and Alarm in credential-encrypted storage (DISC-DATA-MODEL; one database per context). */
@Entity(tableName = "trip")
data class TripEntity(
    @PrimaryKey val id: String,
    val lat: Double,
    val lon: Double,
    val label: String,
    val mode: String,
    val active: Boolean,
)

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val radiusM: Double,
    val state: String,
)

@Dao
interface TripDao {
    @Upsert suspend fun upsertTrip(trip: TripEntity)
    @Upsert suspend fun upsertAlarms(alarms: List<AlarmEntity>)
    @Query("SELECT * FROM trip WHERE active = 1 LIMIT 1") suspend fun activeTrip(): TripEntity?
    @Query("SELECT * FROM alarm WHERE tripId = :tripId") suspend fun alarmsFor(tripId: String): List<AlarmEntity>
    @Query("UPDATE trip SET active = 0 WHERE id = :tripId") suspend fun deactivate(tripId: String)
}

@Database(entities = [TripEntity::class, AlarmEntity::class], version = 1, exportSchema = false)
abstract class TripDatabase : RoomDatabase() {
    abstract fun dao(): TripDao

    companion object {
        @Volatile private var instance: TripDatabase? = null

        /** CE storage: only call after the user has unlocked the device. */
        fun get(context: Context): TripDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, TripDatabase::class.java, "trip.db")
                .build().also { instance = it }
        }
    }
}
