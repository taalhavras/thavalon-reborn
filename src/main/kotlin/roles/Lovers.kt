package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

open class Lover(override val role : RoleType) : Role() {

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getLoverUpdater())
    }

    fun getLoverUpdater() : Updater {
        return Pair(updater@{g : Game ->
            information.add(getLoverInformation(g))
            return@updater
        }, UpdaterPriority.Ten)
    }

    fun getLoverInformation(g : Game) : ThavalonInformation {
        val lusciousLover : RoleType = getOtherLover()
        val otherLover : Role = g.getGoodRoles().find { it.role == lusciousLover } ?:
            return ThavalonInformation.AlertInformation("You are a sad and lonely lover")
        return ThavalonInformation.SingleSeenInformation(otherLover)
    }

    fun getOtherLover() : RoleType {
        return if(role == RoleType.Tristan) RoleType.Iseult else RoleType.Tristan
    }

    override fun prepareInformation(): MutableMap<String, List<String>> {
        val m = super.prepareInformation()

        m["seen"] = m["seen"]!!.map { "You see $it as your luxurious lover ${getOtherLover().role}" }
        return m
    }
}

/**
 * These lovers are ok if there is no other lover in the game
 */
class LoneTristan : Lover(RoleType.Tristan)

class LoneIseult : Lover(RoleType.Iseult)

open class NonLoneLover(override val role : RoleType) : Lover(role) {
    override fun gameOk(g: Game): Boolean {
        return g.getGoodRoles().any {it.role == getOtherLover()}
    }
}

/**
 * These lovers are not ok if there is no other lover in the game
 */
class Tristan : NonLoneLover(RoleType.Tristan)

class Iseult : NonLoneLover(RoleType.Iseult)
