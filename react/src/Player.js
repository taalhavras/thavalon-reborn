import React, { Component } from 'react';
import './css/App.css';
import './css/Player.css';

/**
 * Models the player page. Expects three props: name, which is the name of the player, role, which is
 * the role the game has assigned the player, and role_info, which is the information and text associated
 * with the role.

 */
class Player extends Component {
    render() {
        return ( <div className={"player_info"}>
                <h1 className={"player_title"}> Displaying information for {this.props.location.state.name} </h1>
                <h2>{this.props.location.state.role}</h2>
               <p>{this.props.location.state.role_info}</p>

            </div>

        );
    }
}

export default Player;