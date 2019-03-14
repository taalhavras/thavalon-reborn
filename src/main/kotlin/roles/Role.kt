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
    Nimue,

    Mordred,
    Morgana,
    Maelagant,
    Oberon,
    Agravaine,
    Colgrevance,

    Unknown
}

/**
 * All legal role-alignment pairs
 */
sealed class RoleType(val role : RoleEnum, val alignment : Alignment) {
    object Merlin : RoleType(RoleEnum.Merlin, Alignment.Good)
    object Lancelot : RoleType(RoleEnum.Lancelot, Alignment.Good)
    object Percival : RoleType(RoleEnum.Percival, Alignment.Good)
    object Guinevere : RoleType(RoleEnum.Guinevere, Alignment.Good)
    object Tristan : RoleType(RoleEnum.Tristan, Alignment.Good)
    object Iseult : RoleType(RoleEnum.Iseult, Alignment.Good)
    // need Arthur and NewArthur as separate because the two roles are so different (have different interactions w/oberon)
    object Arthur : RoleType(RoleEnum.Arthur, Alignment.Good)
    object NewArthur : RoleType(RoleEnum.Arthur, Alignment.Good)
    object Titania : RoleType(RoleEnum.Titania, Alignment.Good)
    object Nimue : RoleType(RoleEnum.Nimue, Alignment.Good)

    object Mordred : RoleType(RoleEnum.Mordred, Alignment.Evil)
    object Morgana : RoleType(RoleEnum.Morgana, Alignment.Evil)
    object Maelagant : RoleType(RoleEnum.Maelagant, Alignment.Evil)
    object Oberon : RoleType(RoleEnum.Oberon, Alignment.Evil)
    object Agravaine : RoleType(RoleEnum.Agravaine, Alignment.Evil)
    object Colgrevance : RoleType(RoleEnum.Colgrevance, Alignment.Evil)

    object Unknown : RoleType(RoleEnum.Unknown, Alignment.Unknown)

    override fun toString(): String {
        return "ROLE: $role ALIGNMENT: $alignment"
    }
}

/**
 * All different kinds of information in the game
 * Each role has access to a list of information
 */
sealed class ThavalonInformation {
    // this is used for alerts (aka "you have been oberon'd")
    data class AlertInformation(val alert : String) : ThavalonInformation()

    // this is used to inform you that a role is in the game
    data class RolePresentInformation(val present : Role) : ThavalonInformation()

    // this information represents seeing someone
    data class SingleSeenInformation(val seen : Role) : ThavalonInformation() {
        override fun toString(): String {
            return "${seen.role}"
        }
    }

    // this information represents seeing someone seeing someone else (guinevere info)
    data class ASeesBInformation(var A : Role, var B : Role) : ThavalonInformation() {
        override fun toString(): String {
            return "${A.role} sees ${B.role}"
        }
    }

    // this information represents perfect knowledge of a player and their role (i.e. you know their name and their role)
    data class PerfectInformation(val seen : Role) : ThavalonInformation() {
        override fun toString(): String {
            return "${seen.role}"
        }
    }
}

/**
 * Class to organize and aggregate information. Each role contains an instance of this class
 */
class InformationAggregator {
    // fields for each specific type of ThavalonInformation
    val alerts : MutableList<ThavalonInformation.AlertInformation> = ArrayList()
    val rolePresent : MutableList<ThavalonInformation.RolePresentInformation> = ArrayList()
    val seen : MutableList<ThavalonInformation.SingleSeenInformation> = ArrayList()
    val aSeesB : MutableList<ThavalonInformation.ASeesBInformation> = ArrayList()

    /**
     * As a general note, the reason add and remove return Booleans is so that when(info) performs a pattern match
     * and exhaustively checks all cases. If an uncovered case was there, the functions could potentially return
     * Unit and thus violate their headers.
     */

    fun add(info : ThavalonInformation) : Boolean {
        return when(info) {
            is ThavalonInformation.AlertInformation -> alerts.add(info)
            is ThavalonInformation.RolePresentInformation -> rolePresent.add(info)
            is ThavalonInformation.SingleSeenInformation -> seen.add(info)
            is ThavalonInformation.ASeesBInformation -> aSeesB.add(info)
        }
    }

    fun addAll(infos : Collection<ThavalonInformation>) : Boolean {
        return infos.map { add(it) }.any {it}
    }

    fun remove(info : ThavalonInformation) : Boolean {
        return when(info) {
            is ThavalonInformation.AlertInformation -> alerts.remove(info)
            is ThavalonInformation.RolePresentInformation -> rolePresent.remove(info)
            is ThavalonInformation.SingleSeenInformation -> seen.remove(info)
            is ThavalonInformation.ASeesBInformation -> aSeesB.remove(info)
        }
    }

    fun removeAll(infos : Collection<ThavalonInformation>) : Boolean {
        return infos.map { remove(it) }.any {it}
    }

    override fun toString(): String {
        return "$alerts \n $seen \n $aSeesB \n $rolePresent \n"
    }
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
    val information : InformationAggregator = InformationAggregator()

    // whether the given role is ok with the other roles in the game
    open fun gameOk(g : Game) : Boolean {
        return true
    }

    // gets a list of updaters to fill in this role's necessary information in the game
    open fun getUpdaters(g : Game) : List<Updater> {
        return ArrayList()
    }

    override fun toString(): String {
        return "$role $information\n"
    }
}

/**
 * abstract class for "default" evil roles, adds convenience method for the updater that
 * adds the evil team (minus yourself) as "seen" information
 */
abstract class DefaultEvilRole : Role() {
    private fun getSeesEvilTeamUpdater() : Updater {
        // default evil team, sees all other evil roles that aren't you
        return Pair(updater@{g : Game ->
            information.addAll(g.getEvilRoles()
                .filter { it.role != role || it.role != RoleType.Colgrevance } // see evil team except yourself AND colgrevance
                .map { ThavalonInformation.SingleSeenInformation(it) }) // convert to SingleSeenInformation
            return@updater
        }, UpdaterPriority.Ten)
    }

    override fun getUpdaters(g: Game): List<Updater> {
        return listOf(getSeesEvilTeamUpdater())
    }
}

/**
 * Singleton UnknownRole, used by oberon to corrupt guinevere information
 */
object UnknownRole : Role() {
    override val role : RoleType = RoleType.Unknown
}
