package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority


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
            val potentialTruthInfo : ThavalonInformation.ASeesBInformation = truthGenerator(g) ?: return@updater
            val potentialLieInfo : ThavalonInformation.ASeesBInformation = lieGenerator(g) ?: return@updater
            this.information.add(potentialTruthInfo)
            this.information.add(potentialLieInfo)
        }, UpdaterPriority.Three)
    }

    private fun getAllASeesBInformation(g : Game) : List<ThavalonInformation.ASeesBInformation> {
        return g.rolesInGame
            .filter { it.role !in unseeableRoles } // filter out roles we cannot see
            .map { it.information.seen // map over role's seen information
                .filter { i : ThavalonInformation.SingleSeenInformation -> // filter out seen information that includes unseeable roles
                    i.seen.role !in unseeableRoles }
                .map { i : ThavalonInformation.SingleSeenInformation ->
                ThavalonInformation.ASeesBInformation(it, i.seen) } // transform each singleSeenInfo to an ASeesB information
            }.flatten() // collapse list
    }

    // produces an even distribution across all information in the game instead of all roles
    private fun truthGenerator(g : Game) : ThavalonInformation.ASeesBInformation? {

        val gameInfos : List<ThavalonInformation.ASeesBInformation> = getAllASeesBInformation(g)

        if(gameInfos.isEmpty()) {
            return null
        }
        // return random information in the game
        return gameInfos.random()
    }

    // lie generator across information instead of across roles
    private fun lieGenerator(g : Game) : ThavalonInformation.ASeesBInformation? {
        val rolesInvolvedInLies : List<Role> = g.rolesInGame.filter { it.role !in unseeableRoles }

        val allPotentialLieInformation : MutableSet<ThavalonInformation.ASeesBInformation> = HashSet()

        // construct all possible aSeesBInformation from the game (cartesian product of roles)
        for (r1 in rolesInvolvedInLies) {
            for (r2 in rolesInvolvedInLies) {
                if(r1 != r2) {
                    allPotentialLieInformation.add(ThavalonInformation.ASeesBInformation(r1, r2))
                }
            }
        }
        // get all actual aSeesBInformation from the game
        val gameInfos : List<ThavalonInformation.ASeesBInformation> = getAllASeesBInformation(g)
        // set difference to find lies
        val lieInformation = allPotentialLieInformation.minus(gameInfos)

        // if there are no possible lies, return null. This shouldn't happen under standard rules, but if they are
        // changed it might be possible
        if(lieInformation.isEmpty()) {
            return null
        }
        return lieInformation.random()
    }

}