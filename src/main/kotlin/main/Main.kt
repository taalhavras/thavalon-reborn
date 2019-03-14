package main.kotlin.main

import roles.*
import thavalon.FivesRuleset
import thavalon.Game

    fun Main() {
        main()
    }

    fun main() {
        oberonTitaniaGame()
//    doubleMordredGame()
//    noInfoGwen()
//    fivesGame()
    }

    fun oberonTitaniaGame() {
        val players: MutableList<String> = listOf("Raghu", "Grace", "Kevin", "Josh", "Julius", "May", "George")
                .toMutableList()
        val roles: List<Role> = listOf(Oberon(), Maelagant(), Mordred(), Guinevere(), OldTitania(),
                Merlin(), Lancelot())
        val g: Game = Game(roles, players)
        assert(g.setUp())
        print(g)
    }


    fun doubleMordredGame() {
        val players: MutableList<String> = listOf("Raghu", "Grace", "Kevin", "May", "George")
                .toMutableList()
        val roles: List<Role> = listOf(Mordred(), Mordred(), Tristan(), Iseult(), Guinevere())
        val g: Game = Game(roles, players)
        assert(g.setUp())
        print(g)
    }

    fun noInfoGwen() {
        val players: MutableList<String> = listOf("Raghu", "Grace", "Kevin", "May", "George")
                .toMutableList()
        val roles: List<Role> = listOf(Mordred(), Maelagant(), LoneTristan(), OldPercival(), Guinevere())
        val g: Game = Game(roles, players)
        assert(g.setUp())
        print(g)
    }

    fun fivesGame() {
        val players: MutableList<String> = listOf("Raghu", "Grace", "Kevin", "May", "George")
                .toMutableList()
        val g: Game = FivesRuleset.makeGame(players)
        print(g)
    }
