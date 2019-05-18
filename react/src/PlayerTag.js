import React, { Component } from 'react';
import './css/App.scss';


/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class PlayerTag extends Component {




    render() {
        return ( <div className={"player-tag-wrapper"}>
                <button className={"close_button"} onClick={this.props.change}>X</button>
            <p className={"player_name"}><span className={"name"}> {this.props.name} </span></p>
            </div>

        );
    }
}

export default PlayerTag;