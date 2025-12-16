package com.example.project2

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Enemy(
    val name: String,
    val experience: Int,
    var health: Int,
    val attack: Int,
    val defense: Int,
    val agility: Int,
    val intelligence: Int,
) {
    var observableHealth by mutableIntStateOf(health)

    fun setEnemyHealth(value: Int) {
        observableHealth = value
    }

    fun takeDamage(damage: Int, dataManager: DataManager) {
        observableHealth = (observableHealth - damage).coerceAtLeast(0)
        if (observableHealth == 0) {
            dataManager.advanceEnemy()
        }
    }

    fun enemyAttack(player: Player, dataManager: DataManager): Int {
        val damage = (this.attack - player.defense).coerceIn(0, player.health)
        val newHealth = player.health - damage
        dataManager.setHealth(newHealth)
        return damage
    }
}