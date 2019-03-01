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
        return Pair({g : Game -> obfuscateInformation(g, getTarget(g))}, UpdaterPriority.One)
    }

    private fun getTarget(g : Game) : Role {
        val goodTeam : MutableList<Role> = g.getGoodRoles()
        goodTeam.shuffle()
        // find first member of good team with modifiable information
        return goodTeam.first {
            it.information.any {x : ThavalonInformation -> x is ThavalonInformation.SingleSeenInformation ||
                    x is ThavalonInformation.ASeesBInformation}
        }
    }

    /**
     * Takes in a game and a target, and obfuscates a piece of target's information
     */
    private fun obfuscateInformation(g: Game, target : Role) : Unit {
        assert(target.role.alignment == Alignment.Good)

        val info : MutableList<ThavalonInformation> = target.information
        info.shuffle()
        val oberonAlert = ThavalonInformation.AlertInformation("You have been Oberon'd!")
        val toModify : ThavalonInformation = info.first { it is ThavalonInformation.SingleSeenInformation ||
                it is ThavalonInformation.ASeesBInformation }
        // this is guaranteed to be one of these types. If no such element was present in the list,
        // first would have thrown a NoSuchElementException
        when (toModify) {
            is ThavalonInformation.SingleSeenInformation -> {
                // in this case, we add an additional piece of (false) information to info
                info.add(addSingleInformation(g, target))
            }
            is ThavalonInformation.ASeesBInformation -> {
                // in this case, we want to obfuscate part of toModify
                if (Random.nextBoolean()) {
                    toModify.A = UnknownRole
                } else {
                    toModify.B = UnknownRole
                }
            }
        }

        // add oberon alert
        info.add(oberonAlert)
    }

    /**
     * Helper for adding a false singleSeenInformation to target
     * produces a falseSingleSeen information given a game and a target
     */
    private fun addSingleInformation(g : Game, target : Role) : ThavalonInformation.SingleSeenInformation {
        val alreadySeen : MutableSet<RoleEnum> = HashSet()
        // collect all seen roles from information
        target.information.forEach { if(it is ThavalonInformation.SingleSeenInformation) {
                alreadySeen.add(it.seen.role.role)
            }
        }
        // shuffle roles in game
        val shuffledRoles = g.rolesInGame.shuffled()
        // return random seen information on a role we didn't previously see
        return ThavalonInformation.SingleSeenInformation(shuffledRoles.first { it.role.role !in alreadySeen })
    }

}