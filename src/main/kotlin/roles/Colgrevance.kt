package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

// colgrevance doesn't inherit from DefaultEvilRole but instead just regular role
class Colgrevance : Role() {
    override val role: RoleType = RoleType.Colgrevance

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getColgrevanceUpdater())
    }

    private fun getColgrevanceUpdater() : Updater {
        return Pair(updater@{g : Game ->
            information.addAll(g.getEvilRoles()
                    .filter { it != this } // don't see yourself
                    .map { ThavalonInformation.PerfectInformation(it)}) // convert to PerfectInformation
                    return@updater
        }, UpdaterPriority.Ten)
    }
}