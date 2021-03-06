import React, { Component } from 'react';
import '../css/Live.scss';
import {socket} from "../index.js";
import { Redirect } from 'react-router-dom';

/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Live extends Component {

    constructor(props) {
        super(props);
        console.log(this);
        this.state = {
            joinValid: false,
            lobbyCreateError: "",
            lobbyJoinError: "",
            redirect: "",
            id: "",
            name: ""
        }
    }

    onJoinSubmit = (event) => {
        event.preventDefault();
        if (event.target[0].value.length !== 4) {
            this.setState({lobbyJoinError: "Please enter a game id of length 4"});
            return;
        }

        if (event.target[1].value === "") {
            this.setState({lobbyJoinError: "Please enter a name"});
            return;
        }

        const toSend = {
            type: "JOIN_LOBBY",
            id: event.target[0].value,
            name: event.target[1].value
        };

        this.setState({id: event.target[0].value, name: event.target[1].value});

        socket.send(JSON.stringify(toSend));

    };

    onLobbyCreateSubmit = (event) => {
        event.preventDefault();

        if (event.target[0].value === "") {
            this.setState({lobbyCreateError: "Please enter a name"});
        }

        const toSend = {
            type: "CREATE_LOBBY",
            name: event.target[0].value,

        };

        this.setState({name: event.target[0].value});

        socket.send(JSON.stringify(toSend));
    };

    componentDidMount() {

        socket.addEventListener("message", (message) => {
            console.log("in live event handler");
            console.log(message.data);
            const parsed = JSON.parse(message.data);

            switch (parsed.type) {
                case "NEW_PLAYER":
                    console.log("new player");
                    console.log(parsed.name);
                    break;
                case "REMOVE_PLAYER":
                    console.log("remove player");
                    break;
                case "LOBBY_DELETED":
                    console.log("delete lobby");
                    break;
                case "LOBBY_CREATED":
                    console.log("lobby created");

                    let path = "/live/" +parsed.id;
                    this.setState({redirect: <Redirect to={{pathname: path, state: {name: this.state.name, creator: parsed.name,
                                id: parsed.id, isCreator: true, names: [parsed.name]}}}/>});
                    break;
                case "LOBBY_JOINED":
                    console.log("lobby joined");
                    console.log(parsed);
                    path = "/live/" + this.state.id;
                    console.log(parsed.names);
                    this.setState({redirect: <Redirect to={{pathname: path, state: {name: this.state.name, names: JSON.parse(parsed.names)}}}/>});
                    break;
            }

            // socket.send(JSON.stringify("message recieved"));
        });
    }

    render() {
        return ( <div className={"Live"}>
                {this.state.redirect}
                <form className={"lobby-form"} onSubmit={this.onJoinSubmit}>
                    <input className={"lobby-input"} type={"text"} placeholder={"Enter game code"} autoComplete={"off"}/>
                    <input className={"lobby-input"} type={"text"} placeholder={"Enter name"} autoComplete={"off"}/>
                    <input id={"join-game-submit"} className={"live-button large-button"} type={"submit"} value={"Join Game"}/>
                </form>

                <form className={"lobby-form"} onSubmit={this.onLobbyCreateSubmit}>
                    <input className={"lobby-input"} type={"text"} placeholder={"Enter name"} autoComplete={"off"}/>
                    <input id={"new-game-submit"} className={"live-button large-button"} type={"submit"} value={"Create New Game"}/>
                </form>

            </div>

        );
    }
}

export default Live;