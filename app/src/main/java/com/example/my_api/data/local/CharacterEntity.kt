package com.example.my_api.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val name_id: String,
    val name: String,
    val vision: String,
    val weapon: String,
    val nation: String,
    val affiliation: String,
    val rarity: Int,
    val constellation: String,
    val birthday: String,
    val description: String,
    val card: String,
    val lastUpdated: Long = System.currentTimeMillis()
)