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

    /**
     * @return good roles in the game
     */
    fun getGoodRoles() : MutableList<Role> {
        return rolesInGame.filter { it.role.alignment == roles.Alignment.Good}.toMutableList()
    }

    /**
     * @return evil roles in the game
     */
    fun getEvilRoles() : MutableList<Role> {
        return rolesInGame.filter { it.role.alignment == roles.Alignment.Evil }.toMutableList()
    }

    /**
     * Determines if the ratio of good to evil roles in the game is valid
     * @return boolean representing whether the ratio is valid
     */
    private fun ratioOk() : Boolean {
        val good : List<Role> = getGoodRoles()
        val evil : List<Role> = getEvilRoles()
        return when(rolesInGame.size) {
            5 -> good.size == 3 && evil.size == 2
            7 -> good.size == 4 && evil.size == 3
            8 -> good.size == 5 && evil.size == 3
            10 -> good.size == 6 && evil.size == 4
            else -> false // unsupported ratio
        }
    }

    /**
     * Calls ratioOk and each role's gameOk function to determine if the gamestate is valid
     * @return boolean representing whether the gamestate is valid
     */
    private fun validate() : Boolean {
        // the good-evil ratio is ok and every role is ok with the gamestate
        return ratioOk() && rolesInGame.all { it.gameOk(this) }
    }

    /**
     * Collects updaters from each role, sorts them, and applies them in order
     */
    private fun fillInformation() : Unit {
        val updaters : MutableList<Updater> = ArrayList()
        // collect all updaters
        rolesInGame.forEach { updaters.addAll(it.getUpdaters(this)) }
        // sort them in descending order by their priority
        updaters.sortByDescending { it.second }
        // apply them all in order
        updaters.forEach { it.first(this) }
    }

    /**
     * Assigns all players to roles
     */
    private fun assignPlayers() : Unit {
        assert(rolesInGame.size == players.size)

        for (i in 0 until rolesInGame.size) {
            rolesInGame[i].player.setPlayerName(players[i])
        }
    }

    /**
     * after constructing a game, call this to fill in information
     * @return a boolean indicating success. If false is returned, the game was somehow invalid
     */
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

/**
 * typealias for updater functions
 */
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
