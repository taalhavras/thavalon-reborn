package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

class Guinevere : Role() {
    override val role: RoleType = RoleType.Guinevere

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getLieUpdater(), getTruthUpdater())
    }

    fun getTruthUpdater() : Updater {
        return Pair({g : Game -> val status = this.information.add(truthGenerator(g))}, UpdaterPriority.Three)
    }

    fun getLieUpdater() : Updater {
        return Pair({g : Game -> }, UpdaterPriority.Three)
    }

    private fun truthGenerator(g : Game) : ThavalonInformation.ASeesBInformation? {
        val rolesWithSingleSeen = getRoleWithSingleSeenInfo(g)
        if(rolesWithSingleSeen.isEmpty()) {
            // return null if nobody satisfies truth predicate
            return null
        }
        // alright, so r cannot be mordred OR have a seen information list that only contains mordred
        val r : Role = rolesWithSingleSeen.random()
        val seen : Role = r.information.seen.filter { it.seen.role != RoleType.Mordred }.random().seen
        return ThavalonInformation.ASeesBInformation(r, seen)
    }

    /**
     * Helper to get a role with singleseeninfo from the game
     */
    private fun getRoleWithSingleSeenInfo(g : Game) : List<Role> {
        /**
         *  return all roles in the game with SingleSeenInformation that are NOT mordred OR only contain Mordred.
         *  The first filter removes things that have no SingleSeenInformation, the second filter removes Mordred,
         *  and the last filter removes anyone whose singleSeenInformation list is ONLY MORDRED
         */
        return g.rolesInGame
            .filter { it.information.seen.isNotEmpty() }
            .filter { it.role != RoleType.Mordred }
            .filter { !it.information.seen.all {
                    x : ThavalonInformation.SingleSeenInformation -> x.seen.role == RoleType.Mordred
            } }

    }
}