import React, { Component } from 'react';
import './css/App.css';


/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class PlayerTag extends Component {
    render() {
        return ( <div className={"player-tag-wrapper"}>
                <button className={"close_button"} onClick={this.props.change}>X</button>
                <p className={"player_name"}>{this.props.name}</p>
            </div>

        );
    }
}

export default PlayerTag;