package com.example.my_api.data.repository

import android.util.Log
import com.example.my_api.domain.Character
import com.example.my_api.data.local.AppDatabase
import com.example.my_api.data.local.CharacterEntity
import com.example.my_api.data.local.CharacterDao
import com.example.my_api.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


interface GenshinRepository {
    suspend fun getCharacters(): List<String>
    suspend fun getCharacterDetails(name: String): Character
    suspend fun refreshCharacters()
}

class GenshinRepositoryImpl(private val db: AppDatabase) : GenshinRepository {
    private val api = RetrofitInstance.api
    private val dao = db.characterDao()

    override suspend fun getCharacters(): List<String> = withContext(Dispatchers.IO) {
        val localList = getCachedCharacterList()
        if (localList != null && isCacheValid()) {
            return@withContext localList
        }
        return@withContext fetchAndCacheCharacters()
    }

    override suspend fun getCharacterDetails(name: String): Character = withContext(Dispatchers.IO) {
        val cachedCharacter = dao.getCharacterByName(name)
        if (cachedCharacter != null && isCacheValid()) {
            return@withContext cachedCharacter.toDomain()
        }
        return@withContext fetchAndCacheCharacterDetails(name)
    }

    override suspend fun refreshCharacters() {
        withContext(Dispatchers.IO) {
            dao.clearAll()
            fetchAndCacheCharacters()
        }
    }

    private suspend fun fetchAndCacheCharacterDetails(name: String): Character {
        val remoteCharacter = api.getCharacterDetails(name)
        val entity = remoteCharacter.toEntity()
        dao.insertCharacter(entity)
        return remoteCharacter
    }

    private suspend fun fetchAndCacheCharacters(): List<String> {
        val remoteList = api.getCharacters()
        Log.e("fetchAndCacheCharacters", remoteList[0])
        remoteList.forEach { name_id ->
            val details = api.getCharacterDetails(name_id)
            dao.insertCharacter(details.toEntity())
        }
        return remoteList
    }

    private suspend fun getCachedCharacterList(): List<String>? {
        return dao.getAllCharacters().map { it.name_id }
    }

    private suspend fun isCacheValid(): Boolean {
        val lastUpdate = dao.getAllCharacters().maxOfOrNull { it.lastUpdated } ?: 0
        return System.currentTimeMillis() - lastUpdate < TimeUnit.HOURS.toMillis(24)
    }

    private fun Character.toEntity() = CharacterEntity(
        name_id = id,
        name = name,
        vision = vision,
        weapon = weapon,
        nation = nation,
        affiliation = affiliation,
        rarity = rarity,
        constellation = constellation,
        birthday = birthday,
        description = description,
        card = "https://genshin.jmp.blue/characters/"+id+"/card"
    )

    private fun CharacterEntity.toDomain() = Character(
        id = name_id,
        name = name,
        vision = vision,
        weapon = weapon,
        nation = nation,
        affiliation = affiliation,
        rarity = rarity,
        constellation = constellation,
        birthday = birthday,
        description = description,
        card = card
    )
}