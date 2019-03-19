package roles

class Lancelot : Role() {
    override val role : RoleType = RoleType.Lancelot

    override val description: String
        get() = "You are on the good team. \nAbility: Reversal\nYou can play reverses" +
                " on missions. A reverse inverts the result of a mission: A successful mission will fail and" +
                " a failing mission will succeed."
}