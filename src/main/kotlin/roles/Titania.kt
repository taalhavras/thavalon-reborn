package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority

open class OldTitania : Role() {
    override val role: RoleType = RoleType.Titania

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getTitaniaUpdater())
    }

    private fun getTitaniaUpdater() : Updater {
        return Pair({g : Game -> updateTargets(g, getTargets(g))}, UpdaterPriority.One)
    }

    // old titania targets a single random evil
    open fun getTargets(g : Game) : List<Role> {
        return listOf(g.getEvilRoles().random())
    }

    private fun updateTargets(g: Game, targets : List<Role>) : Unit {
        targets.forEach { updateSingleTarget(g, it) }
    }

    open fun updateSingleTarget(g : Game, r : Role) : Unit {
        assert(r.role.alignment == Alignment.Evil)
        // add a random good role to r's information
        r.information.add(ThavalonInformation.SingleSeenInformation(g.getGoodRoles().random()))
        // inform r that they have been titania'd
        r.information.add(ThavalonInformation.AlertInformation("You have been Titania'd!"))
        // tell titania who they titania'd
        information.add(ThavalonInformation.AlertInformation("You have added false information to a " +
                "player with the role of " + r.role.role.toString()))
    }
}

class NewTitania : OldTitania() {
    // new titania targets all evil
    override fun getTargets(g: Game): List<Role> {
        return g.getEvilRoles()
    }

    override fun updateSingleTarget(g: Game, r: Role) {
        assert(r.role.alignment == Alignment.Evil)
        // new titania adds herself to all evil
        r.information.add(ThavalonInformation.SingleSeenInformation(this))
        // inform r that they have been titania'd
        r.information.add(ThavalonInformation.AlertInformation("You have been Titania'd!"))
    }
}
