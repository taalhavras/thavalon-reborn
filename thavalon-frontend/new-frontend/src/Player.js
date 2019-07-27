import React from "react";
import './styles/Game.scss'
import { url } from './App'
/**
 * Models the Game info.
 */
class Player extends React.Component {

    /**
     * Requests the player data for the game that is happening.
     */


    render() {
        console.log(this.state.location.props);
        return (
            <div className={'Player'}>
                Player
            </div>
        )
    }
}

export default Player;