package main.kotlin.roles

class Lancelot : Role() {
    override val role : RoleType = RoleType.Lancelot

    override fun getDescription(): String {
        return super.getDescription() +  "\nAbility: Reversal\nYou can play reverses" +
                " on missions. A reverse inverts the result of a mission: A successful mission will fail and" +
                " a failing mission will succeed."
    }

    // lance can play reverses
    override fun cardOptions(): List<Card> {
        return listOf(Card.PASS, Card.REVERSE)
    }
}