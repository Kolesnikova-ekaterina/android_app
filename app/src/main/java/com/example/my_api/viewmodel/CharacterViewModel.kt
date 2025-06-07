package com.example.my_api.viewmodel

import androidx.lifecycle.*
import android.util.Log


import com.example.my_api.data.remote.RetrofitInstance
import com.example.my_api.data.repository.GenshinRepository
import com.example.my_api.domain.Character
import kotlinx.coroutines.launch

class CharacterViewModel(private val repository: GenshinRepository) : ViewModel() {

    private val _characters = MutableLiveData<List<String>>()
    val characters: LiveData<List<String>> = _characters

    private val _characterDetails = MutableLiveData<Character>()
    val characterDetails: LiveData<Character> = _characterDetails

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchCharacters() {
        viewModelScope.launch {
            try {
                //val list = RetrofitInstance.api.getCharacters()
                _characters.value = repository.getCharacters()
                _error.value = null
            } catch (e: Exception) {
                Log.e("CharacterViewModel", "Ошибка загрузки списка персонажей", e)
                _error.value = "Ошибка загрузки списка персонажей"
            }
        }
    }

    fun fetchCharacterDetails(name: String) {
        viewModelScope.launch {
            try {
                Log.e("CharacterViewModel", name);
                //val details = RetrofitInstance.api.getCharacterDetails(name)
                _characterDetails.value = repository.getCharacterDetails(name)
                _error.value = null
            } catch (e: Exception) {

                Log.e("CharacterViewModel", "Ошибка загрузки деталей персонажа", e)
                _error.value = "Ошибка загрузки деталей персонажа"
            }
        }
    }
}