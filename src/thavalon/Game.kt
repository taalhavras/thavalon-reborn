package thavalon

import roles.Role

class Player(var name : String) {
    fun setPlayerName(newName : String) : Unit {
        this.name = newName
    }
}

class Game(val rolesInGame : List<Role>, val players : List<String>) {
    fun getGoodRoles() : MutableList<Role> {
        return rolesInGame.filter { it.role.alignment == roles.Alignment.Good}.toMutableList()
    }

    fun getEvilRoles() : MutableList<Role> {
        return rolesInGame.filter { it.role.alignment == roles.Alignment.Evil }.toMutableList()
    }

    fun validate() : Boolean {
        return rolesInGame.all { it.gameOk(this) }
    }

    fun fillInformation() : Unit {
        val updaters : MutableList<Updater> = ArrayList()
        // collect all updaters
        rolesInGame.forEach { updaters.addAll(it.getUpdaters(this)) }
        // sort them in descending order by their priority
        updaters.sortByDescending { it.second }
        // apply them all in order
        updaters.forEach { it.first(this) }
    }

    fun assignPlayers() : Unit {
        assert(rolesInGame.size == players.size)

        for (i in 0 until rolesInGame.size) {
            rolesInGame[i].player.setPlayerName(players[i])
        }
    }

    fun setUp() : Boolean {
        if (!validate()) {
            return false
        }

        fillInformation()
        assignPlayers()
        return true
    }
}


/**
 * Interface for functions that update the game by filling in information
 */
//interface UpdaterFunc {
//    fun update(g : Game) : Unit
//}

typealias UpdaterFunc = (g : Game) -> Unit

/**
 * Enum for updater priorities
 */

enum class UpdaterPriority {
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten
}

/**
 * The type of an updater. Each role can produce a list of these. The second field represents the PRIORITY
 * of the updaterfunc when it comes to being applied to the game. A higher priority means the function
 * will execute before a function with lower priority
 */
typealias Updater = Pair<UpdaterFunc, UpdaterPriority>
