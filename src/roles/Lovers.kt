package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

open class Lover(override val role : RoleType) : Role() {

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getLoverUpdater())
    }

    fun getLoverUpdater() : Updater {
        return Pair({g : Game -> val status = information.add(getLoverInformation(g))}, UpdaterPriority.Ten)
    }

    fun getLoverInformation(g : Game) : ThavalonInformation {
        val lusciousLover : RoleEnum = getOtherLover()
        // the way this interacts with oberon is that it's impossible to oberon a lone lover (since they will
        // have no seen information). I'm not sure if this is the intended behavior. If it is, then we can't
        // send AlertInformation here
        val otherLover : Role = g.getGoodRoles().find { it.role.role == lusciousLover } ?:
            return ThavalonInformation.AlertInformation("You are a sad and lonely lover")
        return ThavalonInformation.SingleSeenInformation(otherLover)
    }

    fun getOtherLover() : RoleEnum {
        return if(role.role == RoleEnum.Tristan) RoleEnum.Iseult else RoleEnum.Tristan
    }
}

/**
 * These lovers are ok if there is no other lover in the game
 */
class LoneTristan : Lover(RoleType.Tristan())

class LoneIseult : Lover(RoleType.Iseult())

open class NonLoneLover(override val role : RoleType) : Lover(role) {
    override fun gameOk(g: Game): Boolean {
        return g.getGoodRoles().any {it.role.role == getOtherLover()}
    }
}

/**
 * These lovers are not ok if there is no other lover in the game
 */
class Tristan : NonLoneLover(RoleType.Tristan())

class Iseult : NonLoneLover(RoleType.Iseult())
