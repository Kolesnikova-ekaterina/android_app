package com.example.my_api.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Query("SELECT * FROM characters WHERE name_id = :name_id")
    suspend fun getCharacterByName(name_id: String): CharacterEntity?

    @Query("SELECT name_id FROM characters")
    suspend fun getAllCharacterIds(): List<String>

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}