package com.hooked.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hooked.data.model.SaveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "hooked_save")

class SaveDataStore(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val SAVE_KEY = stringPreferencesKey("save_blob")
    }

    val saveFlow: Flow<SaveData> = context.dataStore.data.map { pref ->
        pref[SAVE_KEY]?.let {
            runCatching { json.decodeFromString<SaveData>(it) }.getOrDefault(SaveData())
        } ?: SaveData()
    }

    suspend fun write(saveData: SaveData) {
        context.dataStore.edit { pref ->
            pref[SAVE_KEY] = json.encodeToString(saveData)
        }
    }
}
