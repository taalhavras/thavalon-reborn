package main.kotlin.main

import main.kotlin.roles.*
import main.kotlin.thavalon.FivesRuleset
import main.kotlin.thavalon.Game
import main.kotlin.thavalon.makeCustomRuleset

fun main() {
    testCustomGame()
//        oberonTitaniaGame()
//    doubleMordredGame()
//    noInfoGwen()
//    fivesGame()
    }

    fun testCustomGame() {
        val roles : List<String> = listOf("Mordred", "Morgana", "Oberon", "LoneTristan", "LoneIseult", "Percival", "Merlin", "Lancelot")
        val players: MutableList<String> = listOf("Raghu", "Grace", "Kevin", "Josh", "Julius", "May", "George").toMutableList()
        val r = makeCustomRuleset(roles, false)
        print(r.makeGame(players))
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
        val roles: List<Role> = listOf(Mordred(), Maelagant(), LoneTristan(), LonePercival(), Guinevere())
        val g: Game = Game(roles, players)
        assert(g.setUp())
        print(g)
    }

    fun fivesGame() {
        val players: MutableList<String> = listOf("Raghu", "Grace", "Kevin", "May", "George")
                .toMutableList()
        val g: Game = FivesRuleset().makeGame(players)
        print(g)
    }
