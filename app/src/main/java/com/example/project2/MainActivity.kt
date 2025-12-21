package com.example.project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainLayout()
        }
    }
}

@Composable
fun MainLayout() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataManager = remember { DataManager(context) }

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable(
            route = "menu"
        ) {
            MenuScreen(
                onStartGame = {
                    navController.navigate("battle")
                }
            )
        }
        composable(
            route = "battle"
        ) {
            BattleScreen(
                dataManager = dataManager,
                onVictory = {
                    scope.launch {
                        dataManager.handleVictory(dataManager.getEnemy())
                        navController.navigate("rest") {
                            popUpTo("battle") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(
            route = "rest"
        ) {
            RestScreen(
                dataManager = dataManager,
                onContinue = {
                    navController.navigate("battle") {
                        popUpTo("rest") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun RestScreen(dataManager: DataManager, onContinue: () -> Unit) {
    val player by dataManager.playerFlow.collectAsState(
        initial = Player.getDefaultInstance()
    )
    val nextEnemy = dataManager.getEnemy()

    Surface(
        color = Color(0xFF1E1E1E),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = "VICTORY!",
                fontSize = 40.sp,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2C2C2C)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Level: ${player.level}",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "EXP: ${player.experience} / ${player.level * 15}",
                        color = Color.Gray
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = "Next Enemy: ${nextEnemy.name}",
                color = Color.Red,
                fontSize = 24.sp
            )

            Text(
                text = "HP: ${nextEnemy.health} | ATK: ${nextEnemy.attack}",
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(48.dp)
            )

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                )
            ) {
                Text(
                    text = "CONTINUE",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MenuScreen(onStartGame: () -> Unit) {
    Surface(
        color = Color(0xFF121212),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Project 2 (RPG)",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(text = "START GAME", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun BattleScreen(dataManager: DataManager, onVictory: () -> Unit) {
    val player by dataManager.playerFlow.collectAsState(Player.getDefaultInstance())
    val currentEnemy = dataManager.getEnemy()
    var isPlayerTurn by rememberSaveable { mutableStateOf(true) }
    var battleLog by rememberSaveable { mutableStateOf("Your turn!") }

    LaunchedEffect(currentEnemy.observableHealth) {
        if (currentEnemy.observableHealth <= 0) {
            battleLog = "${currentEnemy.name} defeated!"
            kotlinx.coroutines.delay(1000L)
            onVictory()
        }
    }

    LaunchedEffect(isPlayerTurn) {
        if (!isPlayerTurn && currentEnemy.observableHealth > 0 && player.health > 0) {
            battleLog = "${currentEnemy.name} is attacking..."
            kotlinx.coroutines.delay(1000L)
            val damage = currentEnemy.enemyAttack(
                player = player,
                dataManager = dataManager
            )
            battleLog = if (damage > 0) {
                "${currentEnemy.name} hit you for $damage!"
            } else {
                "You dodged the attack!"
            }
            isPlayerTurn = true
        }
    }

    Surface(
        color = Color(0xFF121212),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            EnemySection(currentEnemy)

            Spacer(Modifier.height(24.dp))

            Text(
                text = battleLog,
                color = if (player.health <= 0) Color.Red else Color.White,
                fontSize = 18.sp,
                minLines = 2
            )

            Spacer(Modifier.height(24.dp))
            PlayerStatsSection(player)
            Spacer(Modifier.height(32.dp))

            if (player.health <= 0) {
                Text(
                    text = "GAME OVER",
                    color = Color.Red,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        dataManager.initializeDefaultPlayer()
                        isPlayerTurn = true
                        battleLog = "Revived! Your turn!"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "RESTART"
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val damage = dataManager.playerAttack(
                                player = player,
                                enemy = currentEnemy
                            )
                            battleLog = "You hit ${currentEnemy.name} for $damage!"
                            if (currentEnemy.observableHealth > 0) {
                                isPlayerTurn = false
                            }
                        },
                        enabled = isPlayerTurn && currentEnemy.observableHealth > 0,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Physical Attack"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Button(
                        onClick = {
                            dataManager.castSpell(
                                index = 0,
                                player = player,
                                enemy = currentEnemy
                            )
                            battleLog = "You cast ${dataManager.getSpell(0).name}!"
                            if (currentEnemy.observableHealth > 0) {
                                isPlayerTurn = false
                            }
                        },
                        enabled = isPlayerTurn && player.mana >= 10 && currentEnemy.observableHealth > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${dataManager.getSpell(0).name} (${dataManager.getSpell(0).cost} MP)"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnemySection(enemy: Enemy) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enemy: ${enemy.name}",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "HP: ${enemy.observableHealth} / ${enemy.health}",
            color = Color.Red,
            fontSize = 16.sp
        )
        val enemyRatio = if (enemy.health > 0) {
            enemy.observableHealth.toFloat() / enemy.health.toFloat()
        } else 0f
        LinearProgressIndicator(
            progress = {
                enemyRatio.coerceIn(0f, 1f)
            },
            color = Color.Red,
            modifier = Modifier
                .height(12.dp)
                .fillMaxWidth(0.8f)
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun PlayerStatsSection(player: Player) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Your Level: ${player.level}",
            color = Color.White
        )
        Text(
            text = "Health: ${player.health}",
            color = Color.Green
        )
        val maxHp = 50f + (player.level * 10f)
        LinearProgressIndicator(
            progress = {
                (player.health.toFloat() / maxHp).coerceIn(0f, 1f)
            },
            color = Color.Green,
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Mana: ${player.mana}",
            color = Color.Cyan
        )
        val maxMana = (player.intelligence * 10f).coerceAtLeast(1f)
        LinearProgressIndicator(
            progress = { (player.mana.toFloat() / maxMana).coerceIn(0f, 1f) },
            modifier = Modifier.height(10.dp).fillMaxWidth(),
            color = Color.Cyan
        )
    }
}