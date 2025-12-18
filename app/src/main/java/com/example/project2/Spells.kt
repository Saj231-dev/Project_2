package com.example.project2

class Spells(
    val name: String,
    val damage: Int,
    val cost: Int,
    player: Player
) {
    var mana = player.intelligence * 10

    fun useSpell(enemy: Enemy) {
        when (this.name) {
            "Fireball" -> {
                mana -= 10
            }
        }
        enemy.observableHealth -= this.damage
    }
}