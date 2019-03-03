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

    private fun truthGenerator(g : Game) : ThavalonInformation.ASeesBInformation {
        val r : Role = getRoleWithSingleSeenInfo(g).random()
        val seeingOtherInfo : ThavalonInformation.SingleSeenInformation = r.information.seen.random()
        return ThavalonInformation.ASeesBInformation(r, seeingOtherInfo.seen)
    }

    /**
     * Helper to get a role with singleseeninfo from the game
     */
    private fun getRoleWithSingleSeenInfo(g : Game) : List<Role> {
        val rolesWithSeen = g.rolesInGame.filter { it.information.seen.isNotEmpty() }
        assert(rolesWithSeen.isNotEmpty())
        return rolesWithSeen
    }
}