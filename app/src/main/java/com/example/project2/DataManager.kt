package com.example.project2

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.datastore.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

val Context.playerDataStore by dataStore(
    fileName = "player_schema.pb",
    serializer = PlayerSerializer
)
val Context.enemyIndexDataStore by dataStore(
    fileName = "enemy_index_data.pb",
    serializer = EnemyIndexSerializer
)

class DataManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)

    var index by mutableIntStateOf(0)
        private set

    val playerFlow: Flow<Player> = context.playerDataStore.data

    private val listOfEnemies = mutableStateListOf<Enemy>(
        Enemy("Slime", 5, 20, 2, 1, 1),
        Enemy("Wolf", 7, 25, 4, 2, 2),
        Enemy("Goblin", 10, 40, 6, 2, 2)
    )
    private val listOfSpells = listOf<Spells>(
        Spells("Fireball", 20, 10),
        Spells("Magic Missile", 12, 5)
    )
    init {
        scope.launch {
            context.enemyIndexDataStore.data.collect { savedIndex ->
                index = savedIndex.index
            }
        }
        initializeDefaultPlayer()
    }
    fun initializeDefaultPlayer() {
        scope.launch {
            context.playerDataStore.updateData { currentPlayer ->
                if (currentPlayer == Player.getDefaultInstance()) {
                    Player.newBuilder()
                        .setHealth(50)
                        .setAttack(3)
                        .setDefense(1)
                        .setAgility(1)
                        .setIntelligence(1)
                        .setExperience(0)
                        .setLevel(1)
                        .setMana(10)
                        .build()
                } else {
                    currentPlayer
                }
            }
        }
    }

    fun getEnemy(): Enemy {
        return listOfEnemies[index.coerceIn(0, listOfEnemies.lastIndex)]
    }

    fun getSpell(index: Int): Spells {
        return listOfSpells[index.coerceIn(0, listOfSpells.lastIndex)]
    }

    suspend fun handleVictory(enemy: Enemy) {
        context.playerDataStore.updateData { player ->
            var exp = player.experience + enemy.experience
            var level = player.level
            var atk = player.attack
            var intel = player.intelligence
            var def = player.defense
            var agi = player.agility

            val expNeeded = level * 15
            if (exp >= expNeeded) {
                level += 1
                exp = 0
                atk += 2
                when (Random.nextInt(3)) {
                    0 -> def += 1
                    1 -> agi += 1
                    2 -> intel += 1
                }
            }

            player.toBuilder()
                .setExperience(exp)
                .setLevel(level)
                .setAttack(atk)
                .setIntelligence(intel)
                .setDefense(def)
                .setAgility(agi)
                .setHealth(50 + (level * 10))
                .setMana(intel * 10)
                .build()
        }

        context.enemyIndexDataStore.updateData { currentEnemyIndex ->
            var newIndex = currentEnemyIndex.index + 1
            if (newIndex >= listOfEnemies.size) {
                newIndex = 0
            }
            index = newIndex
            val nextEnemy = listOfEnemies[index]
            nextEnemy.setEnemyHealth(nextEnemy.health)

            currentEnemyIndex.toBuilder()
                .setIndex(newIndex)
                .build()
        }
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

    fun setExperience(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setExperience(value).build()
            }
        }
    }

    fun setLevel(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setLevel(value).build()
            }
        }
    }

    fun setMana(value: Int) {
        scope.launch {
            context.playerDataStore.updateData { player ->
                player.toBuilder().setMana(value).build()
            }
        }
    }

    fun playerAttack(player: Player, enemy: Enemy): Int {
        val damage = (player.attack - enemy.defense).coerceAtLeast(0)
        enemy.takeDamage(damage, this, player)
        return damage
    }

    fun castSpell(index: Int, player: Player, enemy: Enemy) {
        val spell = listOfSpells.getOrNull(index)
        spell?.useSpell(enemy, player, this)
    }
}

