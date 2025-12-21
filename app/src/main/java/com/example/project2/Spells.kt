package com.example.project2

class Spells(
    val name: String,
    val damage: Int,
    val cost: Int
) {
    fun useSpell(enemy: Enemy, player: Player, dataManager: DataManager): Boolean {
        if (player.mana >= this.cost) {
            val newMana = player.mana - this.cost
            dataManager.setMana(newMana)

            val damageDealt = this.damage * player.intelligence
            enemy.takeDamage(damageDealt, dataManager, player)

            return true
        }
        return false
    }
}