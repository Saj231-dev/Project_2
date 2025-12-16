package com.example.project2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.ShortcutInfoCompat
import com.example.project2.ui.theme.Project2Theme
import kotlinx.coroutines.flow.first

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
    val context = LocalContext.current
    val dataManager = DataManager(context)
    val playerState = dataManager.playerFlow.collectAsState(initial = Player.getDefaultInstance())
    val player = playerState.value
    LaunchedEffect(null) {
        dataManager.initializeDefaultPlayer()
    }
    Surface(
        color = Color.Cyan
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ){
            Text(
                text = "Health: ${player.health}",
                fontSize = 32.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Enemy Health: ${dataManager.getEnemy().observableHealth}",
                fontSize = 32.sp
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    dataManager.setHealth(100)
                    dataManager.getEnemy().setEnemyHealth(100)
                }
            ) {
                Text(
                    text = "HEALTH -> 100",
                    fontSize = 24.sp
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    dataManager.getEnemy().enemyAttack(player, dataManager)
                }
            ) {
                Text(
                    text = "This button summons a slime that attacks you, and his name is ${dataManager.getEnemy().name}",
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    dataManager.playerAttack(player, dataManager.getEnemy())
                }
            ) {
                Text(
                    text = "This button attacks ${dataManager.getEnemy().name}, and you did damage",
                    fontSize = 24.sp
                )
            }
        }
    }
}