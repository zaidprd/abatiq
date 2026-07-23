package com.abatiq.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Single DataStore instance per process. Backed by the platform "preferences"
 * store, not proto; keeps the dependency surface tiny for Phase 1.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "abatiq_prefs")

class PreferencesRepository(private val context: Context) {

    private val store: DataStore<Preferences> get() = context.dataStore

    val speechVolumePercent: Flow<Int> = store.data.map { prefs ->
        prefs[VOLUME_KEY] ?: DEFAULT_VOLUME_PERCENT
    }

    suspend fun setSpeechVolumePercent(value: Int) {
        val clamped = value.coerceIn(0, 100)
        store.edit { it[VOLUME_KEY] = clamped }
    }

    companion object {
        const val DEFAULT_VOLUME_PERCENT = 80

        private val VOLUME_KEY = intPreferencesKey("speech_volume_percent")
    }
}