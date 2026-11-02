package io.github.vedantjoshi23.kairos.feature.trip.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import io.github.vedantjoshi23.kairos.feature.trip.domain.Heartbeat
import io.github.vedantjoshi23.kairos.feature.trip.domain.TripBootRecord
import kotlinx.coroutines.flow.first
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import java.io.InputStream
import java.io.OutputStream

/**
 * TripBootRecord in device-protected storage (DISC-DATA-MODEL v1.3.0; STD-DATABASE
 * rule 2). Proto field numbers are fixed and never reused. No coordinates.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TripBootRecordProto(
    @ProtoNumber(1) val tripId: String = "",
    @ProtoNumber(2) val armed: Boolean = false,
    @ProtoNumber(3) val destinationLabel: String = "",
    @ProtoNumber(4) val alarmStates: List<String> = emptyList(),
    @ProtoNumber(5) val hasHeartbeat: Boolean = false,
    @ProtoNumber(6) val heartbeatAtWallMs: Long = 0,
    @ProtoNumber(7) val heartbeatElapsedMs: Long = 0,
    @ProtoNumber(8) val heartbeatBootCount: Int = -1,
)

@OptIn(ExperimentalSerializationApi::class)
internal object BootRecordSerializer : Serializer<TripBootRecordProto> {
    override val defaultValue = TripBootRecordProto()
    override suspend fun readFrom(input: InputStream): TripBootRecordProto = try {
        ProtoBuf.decodeFromByteArray(TripBootRecordProto.serializer(), input.readBytes())
    } catch (e: SerializationException) {
        throw CorruptionException("unreadable boot record", e)
    }

    override suspend fun writeTo(t: TripBootRecordProto, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(TripBootRecordProto.serializer(), t))
    }
}

/** Opened from a device-protected context, so it is readable before the first unlock. */
object BootRecordStore {
    @Volatile private var store: DataStore<TripBootRecordProto>? = null

    fun get(context: Context): DataStore<TripBootRecordProto> = store ?: synchronized(this) {
        store ?: DataStoreFactory.create(
            serializer = BootRecordSerializer,
            corruptionHandler = androidx.datastore.core.handlers.ReplaceFileCorruptionHandler { TripBootRecordProto() },
            produceFile = { context.createDeviceProtectedStorageContext().dataStoreFile("trip_boot.pb") },
        ).also { store = it }
    }

    suspend fun read(context: Context): TripBootRecord? = get(context).data.first().toDomain()

    suspend fun write(context: Context, record: TripBootRecord) {
        get(context).updateData { record.toProto() }
    }

    suspend fun clear(context: Context) {
        get(context).updateData { TripBootRecordProto() }
    }
}

internal fun TripBootRecordProto.toDomain(): TripBootRecord? = if (tripId.isEmpty()) null else TripBootRecord(
    tripId = tripId,
    armed = armed,
    destinationLabel = destinationLabel,
    alarmStates = alarmStates,
    heartbeat = if (hasHeartbeat) {
        Heartbeat(heartbeatAtWallMs, heartbeatElapsedMs, heartbeatBootCount.takeIf { it >= 0 })
    } else {
        null
    },
)

internal fun TripBootRecord.toProto(): TripBootRecordProto = TripBootRecordProto(
    tripId = tripId,
    armed = armed,
    destinationLabel = destinationLabel,
    alarmStates = alarmStates,
    hasHeartbeat = heartbeat != null,
    heartbeatAtWallMs = heartbeat?.atWallMs ?: 0,
    heartbeatElapsedMs = heartbeat?.elapsedMs ?: 0,
    heartbeatBootCount = heartbeat?.bootCount ?: -1,
)
