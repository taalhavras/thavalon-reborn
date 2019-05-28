import React, { Component } from 'react';
import '../css/App.scss';
import {socket} from "../index.js"
import PlayerTag from "../PlayerTag";
import Options from "../Options";
import {Redirect} from "react-router-dom";

/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Lobby extends Component {

    constructor(props) {
        super(props);
        console.log(props);
        console.log(this.props.location.state);
        console.log("lobby");
        const roles =  [
            {key: "Arthur", value: true},
            {key: "Galahad", value: false},
            {key: "Lancelot", value: true},
            {key: "Percival", value: true},
            {key: "Lone Percival", value: false},
            {key: "Guinevere", value: true},
            {key: "Merlin", value: true},
            {key: "Titania", value: true},
            {key: "Nimue", value: false},
            {key: "Gawain", value: false},
            {key: "Lovers", value: true},
            {key: "Lone Lovers", value: false},
            {key: "Mordred", value: true},
            {key: "Morgana", value: true},
            {key: "Maelegant", value: true},
            {key: "Oberon", value: true},
            {key: "Agravaine", value: true},
            {key: "Colgrevance", value: true},
            {key: "Duplicate Roles", value: false}


        ];

        this.state = {
            names: this.props.location.state.names,
            showOptions: false,
            roles: roles,
            redirect: "",
            error: ""
        }
    }


    componentDidMount() {

        socket.addEventListener("message", (message) => {
            const parsed = JSON.parse(message.data);
            switch (parsed.type) {
                case "NEW_PLAYER":
                    console.log("new player");
                    console.log(parsed.name);
                    this.setState({names: this.state.names.concat(parsed.name)});
                    break;
                case "PLAYER_REMOVED":
                    console.log("remove player");
                    this.removePlayer(parsed.name);
                    break;
                case "SELF_REMOVED":
                    // this player in question has been removed
                    console.log("self removed");
                    this.setState({redirect: <Redirect to="/"/>});
                    break;
                case "GAME_STARTED":
                    // game is rolled, redirect to board
                    console.log("game started");
                    this.setState({redirect: <Redirect to={"/" + parsed.id + "/board"}/>});
                    break;
                case "ERROR":
                    console.log("error");
                    this.setState({error: parsed.error});
                    break;
                default:
                    break;
            }

            // socket.send(JSON.stringify("message recieved"));
        });
    }

    onRemoveHandler = (name) => {
        if(name === this.props.location.state.creator) {
            // lobby owner tried to remove themselves, delete lobby
            this.deleteLobby()
        } else {
            this.removePlayer(name);
            socket.send(
                JSON.stringify({
                    type: "REMOVE_PLAYER",
                    name: name,
                    id: this.props.match.params.id
                })
            )
        }

    };

    deleteLobby = () => {
        socket.send(
            JSON.stringify({
                type: "DELETE_LOBBY",
                id: this.props.match.params.id
            })
        )
    };

    removePlayer = (name) => {
        const names = this.state.names.filter(ele => ele !== name);
        this.setState({names: names});
    };

    optionsSubmit = () => {

    };

     /**
     * Redirects to a new game (not custom)
     */
    postToGame = () => {
        let startGameData = {
            type: "START_GAME",
            id: this.props.match.params.id,
            names: this.state.names
        };
        socket.send(JSON.stringify(startGameData));
    };

    render() {
        console.log("lobby");
        console.log(this.state);
        console.log(this.props);
        return (
            <div className={"Lobby"}>
                {this.state.redirect}
                <Options options={this.state.roles} display={this.state.showOptions} submit={this.optionsSubmit}/>

                <h1>Lobby {this.props.match.params.id}</h1>
                {this.props.location.state.isCreator ?
                    <div id={"lobby-owner-view"}>
                        <h2>Players in Lobby</h2>
                        <div className={"player-tag-container"}>
                        {this.state.names.map(ele => {
                            return <PlayerTag name={ele} change={() => this.onRemoveHandler(ele)}/>
                        })}
                        </div>
                        <button  className={"large-button lobby-button"} onClick={() => {this.setState({showOptions: !this.state.showOptions})}}>Game options</button>

                        <button className={"large-button lobby-button"} onClick={this.deleteLobby}>Destroy Lobby</button>

                        <button className={"large-button lobby-button"} onClick={this.postToGame}>Start Game</button>



                    </div>:
                    <div id={"lobby-view"}>
                        <h2>Players in Lobby</h2>
                        <div className={"player-tag-container"}>

                        {this.state.names.map(ele => {
                            return <div className={"live-player-tag"}> {ele}</div>
                        })}
                        </div>
                        <button className={"large-button lobby-button"}>Leave Lobby</button>

                    </div>}
                 <div className={"error"}>{this.state.error}</div>
            </div>

        );
    }
}

export default Lobby;