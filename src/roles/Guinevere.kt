package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority
import java.lang.IllegalStateException

class Guinevere : Role() {
    override val role: RoleType = RoleType.Guinevere()

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
        val seeingOtherInfo : ThavalonInformation = r.information.shuffled().first {
            it is ThavalonInformation.SingleSeenInformation
        }
        when(seeingOtherInfo) {
            is ThavalonInformation.SingleSeenInformation ->
                return ThavalonInformation.ASeesBInformation(r, seeingOtherInfo.seen)
            else ->  throw IllegalStateException("Somehow ended up here")
        }

    }

    /**
     * Helper to get a role with singleseeninfo from the game
     */
    private fun getRoleWithSingleSeenInfo(g : Game) : List<Role> {
        val rolesWithSeen = g.rolesInGame.filter { it.information.any {x: ThavalonInformation ->
            x is ThavalonInformation.SingleSeenInformation} }
        assert(rolesWithSeen.isNotEmpty())
        return rolesWithSeen
    }
}