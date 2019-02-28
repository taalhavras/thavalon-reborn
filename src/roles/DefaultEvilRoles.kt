package roles

/**
 * This class contains the implementations for evil roles whose only information is who else is on the evil team
 */

class Mordred() : DefaultEvilRole() {
    override val role: RoleType = RoleType.Mordred()
}

class Morgana() : DefaultEvilRole() {
    override val role: RoleType = RoleType.Morgana()
}

class Maelagant() : DefaultEvilRole() {
    override val role: RoleType = RoleType.Maelagant()
}

class Agravaine() : DefaultEvilRole() {
    override val role : RoleType = RoleType.Agravaine()
}
