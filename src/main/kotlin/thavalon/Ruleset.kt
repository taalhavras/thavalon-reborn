package main.kotlin.thavalon

import main.kotlin.roles.*
import java.lang.IllegalArgumentException

/**
 * Base class for Thavalon rulesets
 */
open class Ruleset(val goodRoles : List<RoleCreator>, val evilRoles : List<RoleCreator>) {
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
     * Specifies how roles will be drawn. Should randomize
     */
    open fun drawRoles(choices : List<RoleCreator>, num : Int) : List<Role> {
        // we want a random sublist without replacement (no duplicates unless the input contained duplicates)
        return choices.shuffled().subList(0, num).map { it.invoke() }
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
        val roles : List<Role> = drawRoles(goodRoles, numGood).plus(drawRoles(evilRoles, numBad))

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
 * Ruleset that allows duplicate roles
 */
class DuplicateRolesRuleset(goodRoles: List<RoleCreator>, evilRoles: List<RoleCreator>) : Ruleset(goodRoles, evilRoles) {
    override fun drawRoles(choices: List<RoleCreator>, num: Int): List<Role> {
        val l : MutableList<Role> = ArrayList()
        for (c in 0 until num) {
            // choose each role randomly with replacement, thus allowing duplicates
            l.add(choices.random().invoke())
        }
        return l
    }
}

/**
 * typealias for functions that create roles. We use these instead of passing in role objects
 * because for games that allow duplicates it's convenient to be able to mint fresh objects easily
 */
typealias RoleCreator = () -> Role

/**
 * Some common role options for building rulesets
 */

val standardEvil : List<RoleCreator> = listOf(::Mordred, ::Morgana, ::Maelagant, ::Oberon)

/**
 * Standard rulesets for 5, 7, 8, and 10 player games
 */
class FivesRuleset : Ruleset(listOf(::Merlin, ::NewPercival, ::Guinevere, ::Tristan, ::Iseult, ::Lancelot),
    standardEvil)

class SevensRuleset : Ruleset(listOf(::Merlin, ::NewPercival, ::Guinevere, ::Tristan, ::Iseult, ::OldTitania, ::Arthur),
    standardEvil)

class EightsRuleset : Ruleset(listOf(::Merlin, ::NewPercival, ::Guinevere, ::Tristan, ::Iseult, ::OldTitania, ::Arthur),
   standardEvil.plusElement(::Agravaine))

class TensRuleset : Ruleset(listOf(::Merlin, ::NewPercival, ::Guinevere, ::Tristan, ::Iseult, ::OldTitania, ::Arthur),
    standardEvil.plus(listOf(::Agravaine, ::Colgrevance)))
