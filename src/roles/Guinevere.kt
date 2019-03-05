package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority


class Guinevere : Role() {
    override val role: RoleType = RoleType.Guinevere

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getUpdater())
    }

    fun getUpdater() : Updater {
        return Pair(updater@{g : Game ->
            // try to find a truthInformation, if we fail return from lambda
            val potentialTruthInfo : ThavalonInformation.ASeesBInformation = truthGenerator(g) ?: return@updater
            val potentialLieInfo : ThavalonInformation.ASeesBInformation = lieGenerator(g)
            this.information.add(potentialTruthInfo)
            this.information.add(potentialLieInfo)
        }, UpdaterPriority.Three)
    }

    // produces an even distribution across all information in the game instead of all roles
    private fun truthGenerator(g : Game) : ThavalonInformation.ASeesBInformation? {

        val gameInfos : List<ThavalonInformation.ASeesBInformation> = g.rolesInGame
            .filter { it.role != RoleType.Mordred } // filter out mordred's info
            .map { it.information.seen // map over role's seen information
                .filter { i : ThavalonInformation.SingleSeenInformation -> // filter out seen information that includes Mordred/Gwen
                    i.seen.role != RoleType.Mordred || i.seen.role != RoleType.Guinevere }
                .map { i : ThavalonInformation.SingleSeenInformation ->
                ThavalonInformation.ASeesBInformation(it, i.seen) // transform each singleseeninfo to an ASeesB information
            } }
            .flatten() // collapse list

        if(gameInfos.isEmpty()) {
            return null
        }
        // return random information in the game
        return gameInfos.random()
    }

//    private fun truthGenerator(g : Game) : ThavalonInformation.ASeesBInformation? {
//        val rolesWithSingleSeen = getRoleWithSingleSeenInfo(g)
//        if(rolesWithSingleSeen.isEmpty()) {
//            // return null if nobody satisfies truth predicate
//            return null
//        }
//        // alright, so r cannot be mordred OR have a seen information list that only contains mordred
//        val r : Role = rolesWithSingleSeen.random()
//        val seen : Role = r.information.seen.filter { it.seen.role != RoleType.Mordred }.random().seen
//        return ThavalonInformation.ASeesBInformation(r, seen)
//    }

    private fun lieGenerator(g : Game) : ThavalonInformation.ASeesBInformation {
        val role : Role = g.rolesInGame
            .filter { it.role != RoleType.Mordred || it.role != RoleType.Guinevere }.random()
        // roles seen by role
        val seenByRole : Set<Role> = role.information.seen.map { it.seen }.toSet()
        // potentialLies are people unseen by role who aren't role and who aren't mordred or gwen
        val potentialLies : List<Role> = g.rolesInGame
            .filter { it !in seenByRole && it != role && it.role != RoleType.Mordred && it.role != RoleType.Guinevere }
        // If we can't find a potential lie we just recursively call (we'll eventually get one I think)
        if(potentialLies.isEmpty()) {
            return lieGenerator(g)
        }
        return ThavalonInformation.ASeesBInformation(role, potentialLies.random())
    }


//    /**
//     * Helper to get a role with singleseeninfo from the game
//     */
//    private fun getRoleWithSingleSeenInfo(g : Game) : List<Role> {
//        /**
//         *  return all roles in the game with SingleSeenInformation that are NOT mordred OR only contain Mordred.
//         *  The first filter removes things that have no SingleSeenInformation, the second filter removes Mordred,
//         *  and the last filter removes anyone whose singleSeenInformation list is ONLY MORDRED
//         */
//        return g.rolesInGame
//            .filter { it.information.seen.isNotEmpty() }
//            .filter { it.role != RoleType.Mordred }
//            .filter { !it.information.seen.all {
//                    x : ThavalonInformation.SingleSeenInformation -> x.seen.role == RoleType.Mordred
//            } }
//
//    }
}