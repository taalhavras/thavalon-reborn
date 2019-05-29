package main.kotlin.roles

/**
 * This class contains the implementations for evil roles whose only information is who else is on the evil team
 */

class Mordred : DefaultEvilRole() {
    override val role: RoleType = RoleType.Mordred
}

class Morgana : DefaultEvilRole() {
    override val role: RoleType = RoleType.Morgana
}

class Maelegant : DefaultEvilRole() {
    override val role: RoleType = RoleType.Maelegant
    override fun getDescription(): String {
        return super.getDescription() +  "\nAbility: Reversal\nYou can play reverses" +
                " on missions. A reverse inverts the result of a mission: A successful mission will fail and" +
                " a failing mission will succeed."
    }

    // maelegant can play everything
    override fun cardOptions(): List<Card> {
        return listOf(Card.PASS, Card.FAIL, Card.REVERSE)
    }
}

class Agravaine : DefaultEvilRole() {
    override val role : RoleType = RoleType.Agravaine

    // agravaine can only play fails
    override fun cardOptions(): List<Card> {
        return listOf(Card.FAIL)
    }
}
