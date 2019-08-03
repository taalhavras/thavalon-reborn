import React from "react";
import './styles/Game.scss'
import { url } from './App'
import { Link } from 'react-router-dom'

/**
 * Models the Game info.
 */
class Game extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            game: [],
            start: ''
        }

    }

    /**
     * Requests the player data for the game that is happening.
     */
     componentDidMount() {
        const id = this.props.match.params.id;
        fetch(url + "/game/info/" + id, {
            method: "GET"
        }).then(response => {
            return response.json();
        }) .then (data => {
            if(data.length === 0) {
                // invalid id, redirect to homepage
                window.location.href = "/";
            } else {
                console.log(data);
                // found id, just do lookup
                const players = data.map(ele => ele.name);
                this.setState({
                    game: data,
                    start: data[0].name,
                    players: players
                });
            }

        }).catch(error => {
            console.error(error);
        });
    };


    render() {
        return (
            <div className={"Game"}>
                <h1> Game {this.props.match.params.id} </h1>
                <div className={'start block'}>
                    <span className={'game-indent'}> Starting Player: <span className={'bold'}> {this.state.start} </span>
                    </span> </div>
                <div className={'players'}>
                    <ul className={'player-list'}>
                        {this.state.game.map(player => {
                            return (<li className={'block'}>
                                <Link to={{ pathname : this.props.location.pathname + "/" + player.name,
                            state: { info: player, players: this.state.players }}}>
                                    { player.name }
                                </Link>
                                    </li>);
                        })}
                        <li className={'do-not-open block red'}>
                            <Link to={{ pathname : this.props.location.pathname + "/" + "donotopen",
                                state: { info: this.state.game }}}>
                                <span className={'red'}> Do Not Open </span>
                            </Link>
                        </li>
                    </ul>
                </div>

            </div>
        )
    }

}

export default Game;