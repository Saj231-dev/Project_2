package com.example.project2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

data class Enemy (
    val name: String,
    val experience: Int,
    var health: Int,
    val attack: Int,
    val defense: Int,
    val agility: Int,
) {
    var observableHealth by mutableIntStateOf(this.health)

    fun setEnemyHealth(value: Int) {
        observableHealth = value
    }

    fun takeDamage(damage: Int) {
        val dodgeChance = Random.nextInt(51)
        if (dodgeChance > this.agility) {
            observableHealth = (observableHealth - damage).coerceAtLeast(0)
        }
    }

    fun enemyAttack(player: Player, dataManager: DataManager): Int {
        val damage = (this.attack - player.defense).coerceIn(0, player.health)
        val newHealth = player.health - damage
        val dodgeChance = Random.nextInt(51)
        if (dodgeChance > player.agility) {
            dataManager.setHealth(newHealth)
            return damage
        }
        return 0
    }
}