package main.kotlin.roles

import main.kotlin.thavalon.Game
import main.kotlin.thavalon.Updater
import main.kotlin.thavalon.UpdaterPriority

open class Titania : Role() {
    override val role: RoleType = RoleType.Titania

    val untargetableRoles : Set<RoleType> = setOf(RoleType.Colgrevance)

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getTitaniaUpdater(), getOberonPresentUpdater())
    }

    // produces an updater that tells titania if an oberon is in the game
    private fun getOberonPresentUpdater() : Updater {
        return Pair({g : Game ->
            if (g.getEvilRoles().any { it.role == RoleType.Oberon }) {
                information.add(ThavalonInformation.AlertInformation("There is an Oberon in the game!"))
            }
        }, UpdaterPriority.Ten)
    }

    private fun getTitaniaUpdater() : Updater {
        return Pair({g : Game -> updateTargets(g, getTargets(g))}, UpdaterPriority.One)
    }

    // old titania targets a single random evil
    open fun getTargets(g : Game) : List<Role> {
        return listOf(g.getEvilRoles().filter { it.role !in untargetableRoles }.random())
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

class NewTitania : Titania() {
    // new titania targets all evil
    override fun getTargets(g: Game): List<Role> {
        return g.getEvilRoles().filter { it.role !in untargetableRoles }
    }

    override fun updateSingleTarget(g: Game, r: Role) {
        assert(r.role.alignment == Alignment.Evil)
        // new titania adds herself to all evil
        r.information.add(ThavalonInformation.SingleSeenInformation(this))
        // inform r that they have been titania'd
        r.information.add(ThavalonInformation.AlertInformation("You have been Titania'd!"))
    }
}

class TargetsAllTitania : Titania() {
    // targets all
    override fun getTargets(g: Game): List<Role> {
        return g.getEvilRoles().filter { it.role !in untargetableRoles }
    }

    override fun updateSingleTarget(g: Game, r: Role) {
        assert(r.role.alignment == Alignment.Evil)
        // add random good member to all evil
        r.information.add(ThavalonInformation.SingleSeenInformation(g.getGoodRoles().random()))
        // inform r that they have been titania'd
        r.information.add(ThavalonInformation.AlertInformation("You have been Titania'd!"))
    }
}
