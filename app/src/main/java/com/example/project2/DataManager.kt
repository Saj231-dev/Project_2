package com.example.project2

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

val Context.playerDataStore by dataStore(
    fileName = "player.pb",
    serializer = PlayerSerializer
)

class DataManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var index = 0
    val playerFlow: Flow<Player> = context.playerDataStore.data

    val listOfEnemies = mutableStateListOf<Enemy>(
        Enemy("Slime", 5, 25, 5, 2, 1, 1),
    )

    private fun setPlayerStats(player: Player) {
        scope.launch {
            context.playerDataStore.updateData { player }
        }
    }

    fun initializeDefaultPlayer() {
        scope.launch {
            val currentPlayer = playerFlow.first()
            if (currentPlayer == Player.getDefaultInstance()) {
                setPlayerStats(
                    Player.newBuilder()
                        .setHealth(50)
                        .setAttack(3)
                        .setDefense(1)
                        .setAgility(1)
                        .setIntelligence(1)
                        .setExperience(0)
                        .build()
                )
            }
        }
    }

    fun getEnemy(): Enemy {
        return listOfEnemies[index]
    }

    fun advanceEnemy() {
        index++
    }

    fun setHealth(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setHealth(value).build()
            }
        }
    }

    fun setAttack(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setAttack(value).build()
            }
        }
    }

    fun setDefense(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setDefense(value).build()
            }
        }
    }

    fun setAgility(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setAgility(value).build()
            }
        }
    }

    fun setIntelligence(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setIntelligence(value).build()
            }
        }
    }

    fun playerAttack(player: Player, enemy: Enemy): Int {
        val damage = (player.attack - enemy.defense).coerceAtLeast(0)
        enemy.takeDamage(damage, this)
        return damage
    }
}

