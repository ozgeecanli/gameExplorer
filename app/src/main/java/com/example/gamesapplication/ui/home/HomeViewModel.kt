package com.example.gamesapplication.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamesapplication.data.remote.GameInfoStorage
import com.example.gamesapplication.data.remote.model.GameModel
import com.example.gamesapplication.repo.GameRepository
import com.example.gamesapplication.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) :

    ViewModel() {

    private val _games = MutableStateFlow(emptyList<GameModel>())
    val games: StateFlow<List<GameModel>> get() = _games
    private var currentPage = 1
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        getGames()
    }

    private fun getGames() {
        viewModelScope.launch {
            val games = gameRepository.getGames(
                Constants.PAGE_SIZE,
                Constants.PAGE_NUMBER,
                Constants.API_KEY
            )
            if (games != null) {
                _games.value = games
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            if (_loading.value) return@launch // Skip if already loading
            _loading.value = true

            val nextPage = currentPage + 1
            val newGames = gameRepository.getGames(Constants.PAGE_SIZE, nextPage, Constants.API_KEY)
            if (newGames != null) {
                _games.value += newGames
                currentPage = nextPage
            }
            _loading.value = false
        }
    }

    fun setGameInfo(gameModel: GameModel) {
        GameInfoStorage.clearStorage()
        GameInfoStorage.gameModel = gameModel
    }
}