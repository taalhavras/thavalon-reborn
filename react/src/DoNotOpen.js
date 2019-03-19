import React, { Component } from 'react';
import "./css/Player.css";
import { Link } from 'react-router-dom';

/**
 * Models a game page, with links to each player.
 */
class DoNotOpen extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (<div className={"Do Not Open"}>
            <ul>
            {this.props.location.state.game.map(ele => {
                return <li>{ele.name}: {ele.role}, seeing {ele.information}</li>
            })}
            </ul>
        </div>);
    }
}

export default DoNotOpen;