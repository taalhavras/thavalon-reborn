package thavalon

import roles.*
import java.lang.IllegalArgumentException

/**
 * Base class for Thavalon rulesets
 */
open class Ruleset(val goodRoles : List<Role>, val evilRoles : List<Role>) {
    // number of times to reroll before giving up
    private val NUM_REROLLS = 100

    /**
     * Consumes a list of players and produces a game based on the roles in this ruleset.
     * Throws IllegalArgumentException if a valid game cannot be rolled
     */
    fun makeGame(players : MutableList<String>) : Game {
        return makeGameHelper(players, NUM_REROLLS)
    }


    /**
     * Helper for making games that caps the recursion limit (in case some invalid gamestate is presented we
     * will not infinitely loop)
     */
    private fun makeGameHelper(players : MutableList<String>, iter : Int) : Game {
        val numPlayers = players.size
        val ratio : Pair<Int, Int> = getRatio(numPlayers) ?: throw IllegalArgumentException("Bad game ratio")
        val numGood : Int = ratio.first
        val numBad : Int = ratio.second

        if(goodRoles.size < numGood || evilRoles.size < numBad) {
            throw IllegalArgumentException("Game didn't have enough roles")
        }

        // randomly draw the appropriate number of roles
        val roles : List<Role> = goodRoles.shuffled().subList(0, numGood)
            .plus(evilRoles.shuffled().subList(0, numBad))

        val game : Game = Game(roles, players)

        if(!game.setUp()) {
            if(iter == 0) {
                throw IllegalArgumentException("Couldn't set up game")
            }

            // try again
            return makeGameHelper(players, iter - 1)
        }

        return game
    }

    /**
     * Helper function to verify input players are a legal ratio of good to evil
     */
    private fun getRatio(numPlayers : Int) : Pair<Int, Int>? {
        return when(numPlayers) {
            5 -> Pair(3, 2)
            7 -> Pair(4, 3)
            8 -> Pair(5, 3)
            10 -> Pair(6, 4)
            else -> null
        }
    }
}

/**
 * Standard rulesets for 5, 7, 8, and 10 player games
 */
object FivesRuleset : Ruleset(listOf(Merlin(), OldPercival(), Guinevere(), Tristan(), Iseult(), Lancelot()),
    listOf(Mordred(), Morgana(), Maelagant(), Oberon()))

object SevensRuleset : Ruleset(listOf(Merlin(), OldPercival(), Guinevere(), Tristan(), Iseult(), OldTitania(), Arthur()),
    listOf(Mordred(), Morgana(), Maelagant(), Oberon()))

object EightsRuleset : Ruleset(listOf(Merlin(), OldPercival(), Guinevere(), Tristan(), Iseult(), OldTitania(), Arthur()),
    listOf(Mordred(), Morgana(), Maelagant(), Oberon(), Agravaine()))

object TensRuleset : Ruleset(listOf(Merlin(), OldPercival(), Guinevere(), Tristan(), Iseult(), OldTitania(), Arthur()),
    listOf(Mordred(), Morgana(), Maelagant(), Oberon(), Agravaine(), Colgrevance()))
