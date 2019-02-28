package roles

import thavalon.Game
import thavalon.Player
import thavalon.Updater
import thavalon.UpdaterPriority

/**
 * Thavalon Alignments
 */
enum class Alignment {
    Good,
    Evil,
    Unknown
}

enum class RoleEnum {
    Merlin,
    Lancelot,
    Percival,
    Guinevere,
    Tristan,
    Iseult,
    Arthur,
    Titania,

    Mordred,
    Morgana,
    Maelagant,
    Oberon,
    Agravaine,

    Unknown
}

/**
 * All legal role-alignment pairs
 */
sealed class RoleType(val role : RoleEnum, val alignment : Alignment) {
    class Merlin : RoleType(RoleEnum.Merlin, Alignment.Good)
    class Lancelot : RoleType(RoleEnum.Lancelot, Alignment.Good)
    class Percival : RoleType(RoleEnum.Percival, Alignment.Good)
    class Guinevere : RoleType(RoleEnum.Guinevere, Alignment.Good)
    class Tristan : RoleType(RoleEnum.Tristan, Alignment.Good)
    class Iseult : RoleType(RoleEnum.Iseult, Alignment.Good)
    class Arthur : RoleType(RoleEnum.Arthur, Alignment.Good)
    class Titania : RoleType(RoleEnum.Titania, Alignment.Good)

    class Mordred : RoleType(RoleEnum.Mordred, Alignment.Evil)
    class Morgana : RoleType(RoleEnum.Morgana, Alignment.Evil)
    class Maelagant : RoleType(RoleEnum.Maelagant, Alignment.Evil)
    class Oberon : RoleType(RoleEnum.Oberon, Alignment.Evil)
    class Agravaine : RoleType(RoleEnum.Agravaine, Alignment.Evil)

    class Unknown : RoleType(RoleEnum.Unknown, Alignment.Unknown)
}

/**
 * All different kinds of information in the game
 * Each role has access to a list of information
 */
sealed class ThavalonInformation() {
    // this is used for alerts (aka "you have been oberoned")
    data class AlertInformation(val alert : String) : ThavalonInformation()

    // this is used to inform you that a role is in the game
    data class RolePresentInformation(val present : Role) : ThavalonInformation()

    // this information represents seeing someone
    data class SingleSeenInformation(val seen : Role) : ThavalonInformation()

    // this information represents seeing someone seeing someone else (guinevere info)
    data class ASeesBInformation(var A : Role, var B : Role) : ThavalonInformation()
}

/**
 * Abstract class that all roles must extend. Was originally an interface but wanted default initialization of
 * 'player' and 'information' fields, and a default getUpdaters method
 */
abstract class Role {
    // the type and alignment of the role
    abstract val role : RoleType
    // player assigned to this role
    var player : Player = Player("????")

    // the information this role has about the gamestate
    val information : MutableList<ThavalonInformation> = ArrayList()

    // whether the given role is ok with the other roles in the game
    open fun gameOk(g : Game) : Boolean {
        return true
    }

    // gets a list of updaters to fill in this role's necessary information in the game
    open fun getUpdaters(g : Game) : List<Updater> {
        return ArrayList()
    }
}

/**
 * abstract class for "default" evil roles, adds convenience method for the updater that
 * adds the evil team (minus yourself) as "seen" information
 */
abstract class DefaultEvilRole : Role() {
    private fun getSeesEvilTeamUpdater() : Updater {
        // default evil team, sees all other evil roles that aren't you
        return Pair({g : Game -> val status = information.addAll(g.getEvilRoles().filter {
            it.role.role != role.role }.map { ThavalonInformation.SingleSeenInformation(it) })}
            , UpdaterPriority.Ten)
    }

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getSeesEvilTeamUpdater())
    }
}

/**
 * Singleton UnknownRole, used by oberon to corrupt guinevere information
 */
object UnknownRole : Role() {
    override val role : RoleType = RoleType.Unknown()
}
