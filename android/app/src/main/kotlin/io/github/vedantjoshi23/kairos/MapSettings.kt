package io.github.vedantjoshi23.kairos

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settings by preferencesDataStore(name = "settings")

/** Settings fields owned by BC3, stored in Jetpack DataStore (ADR-0006; DISC-DATA-MODEL Settings). */
class MapSettings(private val context: Context) {
    private val skinKey = stringPreferencesKey("default_skin")
    private val flatKey = booleanPreferencesKey("flat_maps")

    fun skin(default: String): Flow<String> = context.settings.data.map { it[skinKey] ?: default }
    fun flat(default: Boolean): Flow<Boolean> = context.settings.data.map { it[flatKey] ?: default }

    suspend fun setSkin(id: String) { context.settings.edit { it[skinKey] = id } }
    suspend fun setFlat(flat: Boolean) { context.settings.edit { it[flatKey] = flat } }
}
