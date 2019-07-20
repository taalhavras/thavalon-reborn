import React, { Component } from 'react';
import "./css/Game.scss";

import { Link } from 'react-router-dom';
import SubmitResults from "./SubmitResults";

import { CopyToClipboard}  from 'react-copy-to-clipboard';
/**
 * Models a game page, with links to each player.
 */
class Game extends Component {

    constructor(props) {
        super(props);
        this.state = {
            game: [],
            start: ""
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
            if(data.length === 0) {
                // invalid id, redirect to homepage
                window.location.href = "/";
            } else {
                // found id, just do lookup
                this.setState({game: data, start: data[0].name});
                return data;
            }

        }).catch(error => {
            console.log(error);
        });
    };


    componentWillMount() {
        this.props.history.push("");
        this.props.history.push(this.props.location);

        this.render_game();
    }


    render() {
        let count = 0;
        console.log(this.props.location);
        return ( <div className="names">
                <h3>Game ID: {this.props.match.params.id} </h3>
                <CopyToClipboard id={"copy"} text={window.location.href}>
                    <button className={"small-button"} id={"copy_button"}>Copy</button>
                </CopyToClipboard>
                <h3> Starting Player: {this.state.start} </h3>

                {this.state.game.map(curr =>
                {
                    const path = this.props.location.pathname + "/" + curr.name;
                    count++;
                    return (<Link key={count} to={{pathname: path, state: {name: curr.name, role: curr.role,
                        role_info: curr.information, description: curr.description}}}>
                        <button className={"large-button"}><span>{curr.name}</span></button>
                    </Link>);
                })}
                <Link to={{pathname: this.props.location.pathname + "/game/donotopen", state: {game: this.state.game}}}>
                    <button className={"large-button"}>Do Not Open</button>
                </Link>
                <Link to={{pathname: "/submitresults", state: {id: this.props.match.params.id}}}>
                    <button className={"large-button"}>Submit Game Results</button>
                </Link>
            </div>

        );
    }
}

export default Game;
