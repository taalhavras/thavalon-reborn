package roles

import thavalon.Game
import thavalon.Player
import thavalon.Updater

/**
 * Thavalon Alignments
 */
enum class Alignment {
    Good,
    Evil
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
    Agravaine
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
    data class ASeesBInformation(val A : Role, val B : Role) : ThavalonInformation()
}

/**
 * Interface that all roles must implement
 */
interface Role {
    // the type and alignment of the role
    val role : RoleType
    // player assigned to this role
    var player : Player

    // the information this role has about the gamestate
    val information : MutableList<ThavalonInformation>

    // whether the given role is ok with the other roles in the game
    fun gameOk(g : Game) : Boolean {
        return true
    }

    // gets a list of updaters to fill in this role's necessary information in the game
    fun getUpdaters(g : Game) : List<Updater>
}

/**
 * Interface for "default" evil roles, adds convenience method for the updater that
 */
interface DefaultEvilRole : Role {

}
