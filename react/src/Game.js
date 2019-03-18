import React, { Component } from 'react';
import "./css/Game.css";
import { Link } from 'react-router-dom';

/**
 * Models a game page, with links to each player.
 */
class Game extends Component {

    constructor(props) {
        super(props);
        this.state = {
            game: []

        }}

    render_game = () => {
        const id = this.props.match.params.id;
        console.log("Get Game Info");
        const url = "/game/info/" + id;
        fetch(url, {
            method: "GET"
        }).then(response => {
                return response.json();
            }) .then (data => {
            console.log("Response");
            console.log(data);
            this.setState({game: data});
            return data;
        }).catch(error => {
            console.log(error);
        });
    };


    componentWillMount() {
        this.render_game();
    }

    componentWillUnmount() {
        clearInterval(this.interval);

    }

    render() {
        let count = 0;
        return ( <div className="names">
                {this.state.game.map(curr =>
                {
                    const path = this.props.location.pathname + "/" + curr.name;
                    count++;
                    return (<Link key ={count} to={{pathname: path, state: {name: curr.name, role: curr.role,
                        role_info: curr.information}}}>
                        <button className={"my_button, large_button"}>{curr.name}</button>
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