package main.kotlin.roles

import main.kotlin.thavalon.Game
import main.kotlin.thavalon.Updater
import main.kotlin.thavalon.UpdaterPriority

open class Lover(override val role : RoleType) : Role() {

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getLoverUpdater())
    }

    private fun getLoverUpdater() : Updater {
        return Pair(updater@{g : Game ->
            information.addAll(getLoverInformation(g))
            return@updater
        }, UpdaterPriority.Ten)
    }

    /**
     * Gets the information for all other lovers you would see in a game. For example, if we have a Tristan
     * and 2 Iseults, the Tristan would see BOTH Iseults while they would each just see Tristan
     */
    private fun getLoverInformation(g : Game) : List<ThavalonInformation> {
        val targetLover : RoleType = getOtherLover()
        val otherLovers : List<Role> = g.getGoodRoles().filter { it.role == targetLover }
        if(otherLovers.isEmpty()) {
            return listOf(ThavalonInformation.AlertInformation("You are a sad and lonely lover"))
        }
        return otherLovers.map { ThavalonInformation.SingleSeenInformation(it) }
    }

    fun getOtherLover() : RoleType {
        return if(role == RoleType.Tristan) RoleType.Iseult else RoleType.Tristan
    }

    /**
     * The lovers have different adjectives for each other! Yay for flavor!
     */
    private fun getLoverAdjective() : String {
        // tristan is seen as luxurious, iseult as luscious
        return if (getOtherLover() == RoleType.Tristan) {
            "luxurious"
        } else {
            "luscious"
        }
    }

    override fun prepareInformation(): MutableMap<String, List<String>> {
        val m = super.prepareInformation()

        m["seen"] = m["seen"]!!.map { "You see $it as your ${getLoverAdjective()} lover ${getOtherLover().role}" }
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
