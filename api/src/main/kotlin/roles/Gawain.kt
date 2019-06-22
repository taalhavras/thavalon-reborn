package main.kotlin.roles

import main.kotlin.thavalon.Game
import main.kotlin.thavalon.Updater
import main.kotlin.thavalon.UpdaterPriority
import kotlin.random.Random

/**
 * Class for Gawain. Gawain sees two pairs of players. One pair is on the same team and the
 * other pair are on different teams. This version will allow overlap between the pairs, though
 * other versions might ban this based on balance reasons.
 */
class Gawain : Role() {
    override val role: RoleType = RoleType.Gawain

    // set of roles that gawain cannot see information about
    private val unseeableRoles : Set<RoleType> = setOf(RoleType.Mordred)

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getGawainUpdater())
    }

    private fun getGawainUpdater() : Updater {
        return Pair(updater@{g : Game ->
            // try to get both pieces of information, if we cannot then just return
            val sameTeam = getSameTeamPair(g) ?: return@updater
            val diffTeam = getDifferentTeamPair(g) ?: return@updater

            // add information
            information.addAll(listOf(sameTeam, diffTeam))
            return@updater
        }, UpdaterPriority.Ten)
    }

    private fun getSameTeamPair(roles : List<Role>) : ThavalonInformation.PairSeenInformation? {
        val candidateRoles : List<Role> = roles
            .filter { it != this }
            .filter { it.role !in unseeableRoles }
            .shuffled()
        if(candidateRoles.size < 2) {
            return null
        }
        return ThavalonInformation.PairSeenInformation(candidateRoles[0], candidateRoles[1])
    }

    private fun getSameTeamPair(g : Game) : ThavalonInformation.PairSeenInformation? {
        // randomly decide if we are looking at good or evil roles
        // in both cases, if we fail to find a pair for that alignment, we try the
        // other alignment
        return if(Random.nextBoolean()) {
            // try a good pair first, try evil if it fails
            getSameTeamPair(g.getGoodRoles()) ?: getSameTeamPair(g.getEvilRoles())
        } else {
            // try an evil pair first, try good if it fails
            getSameTeamPair(g.getEvilRoles()) ?: getSameTeamPair(g.getGoodRoles())
        }
    }

    private fun getDifferentTeamPair(g : Game) : ThavalonInformation.PairSeenInformation? {
        // try to get a pair, if there aren't any valid seeable roles for either the good or either team, return null
        return try {
            // get random good role from game that isn't us
            val goodRole: Role = g.getGoodRoles().filter { it != this }.filter { it.role !in unseeableRoles }.random()
            // get random evil role that isn't mordred
            val evilRole: Role = g.getEvilRoles().filter { it.role !in unseeableRoles }.random()

            // randomize order we put the roles in the information
            if (Random.nextBoolean()) {
                ThavalonInformation.PairSeenInformation(goodRole, evilRole)
            } else {
                ThavalonInformation.PairSeenInformation(evilRole, goodRole)
            }
        } catch (e : NoSuchElementException) {
            null
        }
    }

    override fun prepareInformation(): MutableMap<String, List<String>> {
        val m = super.prepareInformation()
        m["pairSeen"] = m["pairSeen"]!!
            .map { it.replace("/", " and ") }
            .map { "$it are potentially allies (or enemies)" }
        return m
    }
}