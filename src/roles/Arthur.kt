package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

/**
 * Class for standard Arthur, night phase declaration version
 */
open class Arthur : Role() {
    override val role : RoleType = RoleType.Arthur
}

/**
 * Class for new Arthur, knows all good roles in game
 */
class NewArthur : Arthur() {
    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getArthurUpdater())
    }

    private fun getArthurUpdater() : Updater {
        return Pair(updater@{g : Game ->
            val infos : List<ThavalonInformation.RolePresentInformation> = g.rolesInGame
                .filter { it != this } // filter our self
                .map { ThavalonInformation.RolePresentInformation(it) } // convert to information
            information.addAll(infos) // add to our information
            return@updater
        }, UpdaterPriority.Ten)
    }
}

