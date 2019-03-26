import React, { Component } from 'react';
import './css/App.css';


/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Rules extends Component {
        render() {
        return (

            <div className={"Rules"} id={"rules"}>




                {/*<ScrollableAnchor id={"overview"}>  <div> </div> </ScrollableAnchor>*/}
                <div id={"overview"}> <h2>Overview</h2> </div>

                THavalon is a custom ruleset for the social deception game Resistance: Avalon. If you've played Avalon before,
                the basic structure of the game is the same, although there are many more roles.

                {/*<ScrollableAnchor id={"missions"}> <div> </div> </ScrollableAnchor>*/}

                <div id={"missions"}> <h2>Good, Evil, and Missions</h2> </div>
                In THavalon, there are two teams, Good and Evil, who win depending on the outcome of five missions. For each mission,
                a certain number of players are sent. These players each select a mission card (fail, pass, or reverse) to play,
                and all cards played on the mission are then revealed to all players.

                If there is one fail card played, even if all the rest of the cards are successes, the mission fails. If a reverse card
                is played, the result of the mission is reversed (e.g. a failing mission will become a passing mission, or vise verse).
                Only Lancelot and Maelagant can play reverse cards.

                Good wins if they pass three missions, and survive assasination. Evil wins if they fail three missions.

                {/*<ScrollableAnchor id={"proposals"}> <div> </div> </ScrollableAnchor>*/}

                    <div id={"proposals"}> <h2>Proposals</h2> </div>

                To determine who goes on missions, players make proposals, which are then voted on. The first mission is a binary choice
                between proposals made by the two players before the starting player, which are not discussed. After that, beginning with
                the starting player, proposals travel clockwise, moving to the next person if they are voted down.

                There are a limited number of missions that can be proposed per round (3 in 5 players, 4 in 7 and 8 player, and 5 in 10 player).
                The last possible proposal is sent automatically.

                {/*<ScrollableAnchor id={"assasination"}>  <div> </div> </ScrollableAnchor>*/}

                <h2>Assassination</h2>

                If Good has successfully passed three missions, Evil then has the chance to assasinate. To assasinate, can either
                name a pair of Lovers, Merlin, Nimue if included, or claim there are no assasinable targets in the game. If Evil
                selects correctly, they win. If not, Good wins.
                {/*<ScrollableAnchor id={"hijack"}>  <div> </div> </ScrollableAnchor>*/}

                <h2>Inquisition and Hijack</h2>

                With 7+ players, two new mechanics come into play: weak inquisition and hijack.

                Weak inquisition happens if the first proposal of a round is sent. The player who sent the mission can then select one
                other player, and view their card, showing it to no one else.

                Hijack happens when the last proposal of a round is sent. An evil player will have the ability "Hijack" declared in their
                information. This player may then reveal themselves as evil, and remove a player from the mission, inserting themselves
                on.

                {/*<ScrollableAnchor id={"good_roles"}> <div> </div>  </ScrollableAnchor>*/}

                <h2>Good Roles</h2>
                Merlin: sees all players that are either Evil (except Mordred) or are Lancelot; can be Assassinated. <br></br>

                Percival: sees Merlin and Morgana, but cannot distinguish which role each seen player has. <br></br>

                Tristan and Iseult: see each other; always appear together; can be Assassinated as a pair. <br></br>

                Lancelot: may play Reversal cards while on missions; appears Evil to Merlin. <br></br>

                Nimue (5) : knows which roles are in the game; can be Assassinated. <br></br>

                Arthur (7+): knows which Good roles are present; may declare after 2 Failed
                    and 0-1 Successful missions to make their votes on mission proposals count twice,
                    but lose the ability to be on mission teams until the 5th mission. <br></br>

                Titania (7+): adds false information to one Evil player's information. <br></br>
                    {/*<ScrollableAnchor id={"evil_roles"}>  <div> </div>  </ScrollableAnchor>*/}
                <h2>Evil Roles</h2>
                    Mordred: is hidden from Merlin. <br></br>

                Morgana: appears like Merlin to Percival. <br></br>

                Maelagant: may play Reversal cards while on missions. <br></br>

                Oberon: adds false information to one Good player.

                Agravaine (8+): must play Fails; may declare after having been on a successful mission to cause it to Fail instead. <br></br>

                Colgrevance (10): is hidden from other Evil roles; knows which player has each Evil role. <br></br>



            </div>);
    }
}

export default Rules;