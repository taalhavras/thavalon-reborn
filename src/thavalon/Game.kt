package thavalon

import roles.Role

class Player(var name : String) {
    fun setPlayerName(newName : String) : Unit {
        this.name = newName
    }
}

class Game(val rolesInGame : List<Role>, val players : MutableList<String>) {
    // init block shuffles players
    init {
        players.shuffle()
    }

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

    override fun toString(): String {
        return rolesInGame.toString()
    }
}

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
