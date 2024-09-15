package net.brenig.pixelescape.game.player.abliity

/**
 * Easy access class for instances of [Ability]
 */
object Abilities {
    val BLINK = AbilityBlink()
    val BLINK_LONG_COOLDOWN = AbilityBlink(10f)
}
