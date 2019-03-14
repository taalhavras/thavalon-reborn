import React, { Component } from 'react';
import "./css/Game.css";
import { Link } from 'react-router-dom';

/**
 * Models a game page, with links to each player.
 * Expects a names prop, which is an array of String names of players,
 */
class Game extends Component {

    render() {

        console.log(this.props);
        let count = 0;
        if (this.props.location.state.names === undefined) {
          return null;
        }
        console.log(this.props.location.state.num);

        return ( <div className="names">
                {this.props.location.state.names.map(curr =>
                {
                    const path = this.props.location.pathname + "/" + curr;
                    console.log(path);
                    count++;
                    return (<Link key ={count} to={{pathname: path, state: {name: curr.toString(), role: "You are Lorem Ipsum",
                        role_info: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"}}}>
                        <button className={"my_button, large_button"}>{curr.toString()}</button>
                    </Link>);
                })}
                <Link key ={count} to={{pathname: "/donotopen", state: {name: "Do Not Open", role: ""}}}>
                    <button className={"my_button, large_button"}>Do Not Open</button>
                </Link>
            </div>

        );
    }
}

export default Game;