import React, { Component } from 'react';
import './css/App.scss';


/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Rules extends Component {
        render() {
        return (

            <div className={"Rules"} id={"rules"}>
                <h1>Rules</h1>




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
                Only Lancelot and Maelegant can play reverse cards.

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

                    <h2> Roles </h2>
                    <div className={"italics"}>Note: by default the following roles are included in the game,
                            with no duplicates or Lone Lovers
                            <div className={"break"}> 5 players: Lancelot, Merlin, the Lovers, Percival, Guinevere, Mordred, Morgana, Maelegant, Oberon </div>
                            <div className={"break"}> 7 players: add Titania and Arthur</div>
                            <div className={"break"}> 8 players: add Agravaine </div>
                            <div className={"break"}> 10 players: add Colgrevance </div>




                    </div>

                <h2>Good Roles</h2>

                    <ul className={"roles-list"}>
                            <li> <em> Arthur: </em> knows which Good roles are present in the game. </li>
                            <li> <em>Galahad: </em> Can declare after two failed missions. Then may conduct a 'night phase' where they request
                                    two good roles, who then reveal themselves to Galahad only.</li>
                            <li> <em> Gawain: </em> Sees two pairs of players, one pair is on the same team, one is on opposite teams.
                            </li>
                            <li> <em> Guinevere: </em> Sees two rumors in the form of "A sees B" where A and B are players, one of which
                                    is true, one of which is a lie.
                            </li>
                            <li> <em> Lancelot: </em> may play reverses while on missions; appears evil to Merlin. </li>
                            <li> <em> The Lovers (Tristan and Iseult): </em> see each other,
                                    always appear together (unless Lone Lovers is enabled),
                                    and can be assassinated as a pair. </li>
                            <li> <em> Merlin: </em> sees all players that are either evil
                                    (except Mordred) or are Lancelot, can be assassinated.</li>
                            <li> <em> Nimue: </em> knows which roles are in the game; can be Assassinated. </li>
                            <li> <em> Percival: </em> sees both Merlin and Morgana,
                                    but cannot distinguish which role each seen player has. </li>
                            <li> <em> Titania:</em> adds false information to one Evil player's information.
                            </li>
                    </ul>

                <h2>Evil Roles</h2>
                    <ul className={"roles-list"}>
                    <li> <em> Mordred:</em> is hidden from Merlin.</li>
                    <li> <em> Morgana: </em> appears like Merlin to Percival. </li>
                    <li> <em> Maelegant:</em> may play Reversal cards while on missions.</li>
                    <li> <em> Oberon: </em> adds false information to one Good player. </li>
                    <li> <em> Agravaine: </em> must play Fails; may declare after having been on a successful mission to cause it to Fail instead.
                    </li>
                    <li> <em> Colgrevance </em> is hidden from other Evil roles; knows which player has each Evil role.
                    </li>
                    </ul>
                

            </div>);
    }
}

export default Rules;