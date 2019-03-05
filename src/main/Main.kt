package main

import roles.*
import thavalon.Game

fun main() {
    val players : MutableList<String> = listOf("Raghu", "Grace", "Kevin", "Josh", "Julius", "May", "George")
        .toMutableList()
    val roles : List<Role> = listOf(Oberon(), Maelagant(), Mordred(), Guinevere(), OldTitania(),
        Merlin(), Lancelot())
    val g : Game = Game(roles, players)
    assert(g.setUp())
    // TODO fix infinite recursion loop w/toString
    print(g)
}