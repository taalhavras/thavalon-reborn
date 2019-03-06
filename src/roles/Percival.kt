package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

/**
 * Old percival implementation. Doesn't care if they don't see anyone
 */
open class OldPercival : Role() {
    override val role: RoleType = RoleType.Percival

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(makePercivalUpdater())
    }

    fun seenByPercival(r : Role) : Boolean {
        return r.role == RoleType.Morgana || r.role == RoleType.Merlin
    }

    fun makePercivalUpdater() : Updater {
        return Pair(updater@{g : Game ->
            this.information.addAll(g.rolesInGame.filter { seenByPercival(it)}
                .map { ThavalonInformation.SingleSeenInformation(it)})
            return@updater
        }, UpdaterPriority.Nine)
    }

}

/**
 * New Percival implementation, must see at least one role
 */
class NewPercival() : OldPercival() {
    override fun gameOk(g: Game): Boolean {
        return g.rolesInGame.any {seenByPercival(it)}
    }
}