package main.kotlin.roles

import main.kotlin.thavalon.Game
import main.kotlin.thavalon.Updater
import main.kotlin.thavalon.UpdaterPriority


class Guinevere : Role() {
    override val role: RoleType = RoleType.Guinevere

    // set of roles that gwen cannot see rumors about
    private val unseeableRoles : Set<RoleType> = setOf(RoleType.Guinevere, RoleType.Mordred)

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getUpdater())
    }

    fun getUpdater() : Updater {
        return Pair(updater@{g : Game ->
            // try to find a truth information and a lie information if we fail return from lambda
            val potentialTruthInfo : ThavalonInformation.PairSeenInformation = truthGenerator(g) ?: return@updater
            val potentialLieInfo : ThavalonInformation.PairSeenInformation = lieGenerator(g) ?: return@updater
            // add information
            information.addAll(listOf(potentialLieInfo, potentialTruthInfo))
        }, UpdaterPriority.Three)
    }

    private fun getAllPairSeenInformation(g : Game) : List<ThavalonInformation.PairSeenInformation> {
        return g.rolesInGame
            .filter { it.role !in unseeableRoles } // filter out roles we cannot see
            .map { r : Role -> r.information.seen // map over role's seen information
                .filter { i : ThavalonInformation.SingleSeenInformation -> // filter out seen information that includes unseeable roles
                    i.seen.role !in unseeableRoles }
                .map { i : ThavalonInformation.SingleSeenInformation ->
                ThavalonInformation.PairSeenInformation(r, i.seen) } // transform each singleSeenInfo to an PairSeen information
            }.flatten() // collapse list
    }

    // produces an even distribution across all information in the game instead of all roles
    private fun truthGenerator(g : Game) : ThavalonInformation.PairSeenInformation? {

        val gameInfos : List<ThavalonInformation.PairSeenInformation> = getAllPairSeenInformation(g)

        if(gameInfos.isEmpty()) {
            return null
        }
        // return random information in the game
        return gameInfos.random()
    }

    // lie generator across information instead of across roles
    private fun lieGenerator(g : Game) : ThavalonInformation.PairSeenInformation? {
        val rolesInvolvedInLies : List<Role> = g.rolesInGame.filter { it.role !in unseeableRoles }

        val allPotentialLieInformation : MutableSet<ThavalonInformation.PairSeenInformation> = HashSet()

        // construct all possible PairSeenInformation from the game (cartesian product of roles)
        for (r1 in rolesInvolvedInLies) {
            for (r2 in rolesInvolvedInLies) {
                if(r1 != r2) {
                    allPotentialLieInformation.add(ThavalonInformation.PairSeenInformation(r1, r2))
                }
            }
        }
        // get all actual gwen info from the game
        val gameInfos : List<ThavalonInformation.PairSeenInformation> = getAllPairSeenInformation(g)
        // set difference to find lies
        val lieInformation = allPotentialLieInformation.minus(gameInfos)

        // if there are no possible lies, return null. This shouldn't happen under standard rules, but if they are
        // changed it might be possible
        if(lieInformation.isEmpty()) {
            return null
        }
        return lieInformation.random()
    }

    override fun prepareInformation(): MutableMap<String, List<String>> {
        val m = super.prepareInformation()
        // format pairSeen info into "A" sees "B"
        m["pairSeen"] = m["pairSeen"]!!.map { it.replace("/", " sees ") }
        return m
    }
}