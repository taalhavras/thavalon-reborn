import React from "react";
import './styles/Game.scss'
import { url } from './App'
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
                this.setState({game: data, start: data[0].name});
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
                    Starting Player: <span className={'bold'}> {this.state.start} </span> </div>
                <div className={'players'}>
                    <ul className={'player-list'}>
                        {this.state.game.map(player => {
                            return <li className={'block'}><Link to={{window  .+ player.name}</li>
                        })}
                        <li className={'do-not-open block red'}>Do Not Open</li>
                    </ul>
                </div>

            </div>
        )
    }

}

export default Game;