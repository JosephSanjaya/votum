package io.votum.auth.data.local

import io.votum.auth.data.model.AuthData
import io.votum.core.data.LocalDataSource
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class AuthLocalDataSource(
    private val localDataSource: LocalDataSource,
    private val json: Json
) {
    fun saveVoterData(authData: AuthData) {
        val jsonData = json.encodeToString(authData)
        localDataSource.putString(VOTER_DATA_KEY, jsonData)
    }

    fun getVoterData(): AuthData? {
        val jsonData = localDataSource.getString(VOTER_DATA_KEY, "")
        return json.runCatching { decodeFromString<AuthData>(jsonData) }.getOrNull()
    }

    fun isLoggedIn(): Boolean {
        return localDataSource.hasKey(VOTER_DATA_KEY)
    }

    fun clear() {
        localDataSource.remove(VOTER_DATA_KEY)
    }

    companion object {
        private const val VOTER_DATA_KEY = "voter_data"
    }
}
