package roles

import thavalon.Game
import thavalon.Updater
import thavalon.UpdaterPriority
import kotlin.random.Random

class Oberon : DefaultEvilRole() {
    override val role = RoleType.Oberon()

    override fun getUpdaters(g: Game): List<Updater> {
        val updaters : MutableList<Updater> = super.getUpdaters(g).toMutableList()
        updaters.add(getOberonUpdater())
        return updaters
    }

    private fun getOberonUpdater() : Updater {
        // oberon updater must run last
        return Pair({g : Game -> obfuscateInformation(g, getTarget(g))}, UpdaterPriority.One)
    }

    private fun getTarget(g : Game) : Role {
        val goodTeam : MutableList<Role> = g.getGoodRoles()
        goodTeam.shuffle()
        // find first member of good team with modifiable information
        return goodTeam.first {
            it.information.seen.isNotEmpty() || it.information.aSeesB.isNotEmpty()
        }
    }

    /**
     * Takes in a game and a target, and obfuscates a piece of target's information
     */
    private fun obfuscateInformation(g: Game, target : Role) : Unit {
        assert(target.role.alignment == Alignment.Good)

        val oberonAlert = ThavalonInformation.AlertInformation("You have been Oberon'd!")


        if (target.information.seen.isEmpty()) {
            target.information.add(modifyASeesBInformation(g, target))
        } else if(target.information.aSeesB.isEmpty()) {
            target.information.add(addSingleInformation(g, target))
        } else {
            // this case is only hit if there's a role with both kinds of information
            // no such roles exist yet, but since we might add one I wrote this anyway
            if (Random.nextBoolean()) {
                 target.information.add(modifyASeesBInformation(g, target))
            } else {
                 target.information.add(addSingleInformation(g, target))
            }
        }

        // add oberon alert
        target.information.add(oberonAlert)
    }

    /**
     * Helper for adding a false singleSeenInformation to target
     * produces a falseSingleSeen information given a game and a target
     */
    private fun addSingleInformation(g : Game, target : Role) : ThavalonInformation.SingleSeenInformation {
        assert(target.information.seen.isNotEmpty())
        val alreadySeen : MutableSet<RoleEnum> = HashSet()
        // collect all seen roles from information
        target.information.seen.forEach {
            alreadySeen.add(it.seen.role.role)
        }
        // shuffle roles in game
        val shuffledRoles = g.rolesInGame.shuffled()

        // assert that no role can already see everyone
        assert(alreadySeen.size < shuffledRoles.size)

        // return random seen information on a role we didn't previously see
        return ThavalonInformation.SingleSeenInformation(shuffledRoles.first { it.role.role !in alreadySeen })
    }

    private fun modifyASeesBInformation(g : Game, target : Role) : ThavalonInformation.ASeesBInformation {
        assert(target.information.aSeesB.isNotEmpty())
        val toModify : ThavalonInformation.ASeesBInformation = target.information.aSeesB.random()
        if (Random.nextBoolean()) {
            toModify.A = UnknownRole
        } else {
            toModify.B = UnknownRole
        }
        return toModify
    }

}