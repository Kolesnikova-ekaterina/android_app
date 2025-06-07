package com.example.my_api.domain

data class Character(
    val id: String,
    val name: String,
    val vision: String,
    val weapon: String,
    val nation: String,
    val affiliation: String,
    val rarity: Int,
    val constellation: String,
    val birthday: String,
    val description: String,
    val card: String
)