package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

class Nimue : Role() {
    override val role : RoleType = RoleType.Nimue

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getNimueUpdater())
    }

    private fun getNimueUpdater() : Updater {
        return Pair(updater@{g : Game ->
            val nimueInformation : List<ThavalonInformation.RolePresentInformation> =
                g.rolesInGame
                    .filter { it != this } // remove ourselves from the game (don't want info on us)
                    .map { ThavalonInformation.RolePresentInformation(it) } // transform into appropriate info
            // add all information to ourself
            information.addAll(nimueInformation)
            return@updater
        }, UpdaterPriority.Ten)
    }
}
