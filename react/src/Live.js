import React, { Component } from 'react';
import './css/App.css';
import { socket } from "./index.js";
import { Redirect } from 'react-router-dom';

/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Live extends Component {

    constructor(props) {
        super(props);
        this.state = {
            joinValid: false,
            lobbyCreateError: "",
            lobbyJoinError: "",
            redirect: ""
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


        socket.send(JSON.stringify(toSend));

    };

    onLobbyCreateSubmit = (event) => {
        event.preventDefault();

        if (event.target[0].value === "") {
            this.setState({lobbyCreateError: "Please enter a name"});
        }

        const toSend = {
            type: "CREATE_LOBBY",
            name: event.target[0].value
        };

        socket.send(JSON.stringify(toSend));
    };

    componentDidMount() {

        socket.addEventListener("message", (message) => {
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
                    console.log(parsed.id);
                    let path = this.props.location.pathname + "/live/" + parsed.id;
                    this.setState({redirect: <Redirect to={{pathname: path, state: {creator: parsed.name,
                                id: parsed.id}}}/>});
                    break;
                case "LOBBY_JOINED":
                    console.log("lobby joined");
                    path = this.props.location.pathname + "/live/" + parsed.id;
                    this.setState({redirect: <Redirect to={{pathname: path}}/>});
                    break;
                default:
                    console.log("message type not recognized");
            }

            // socket.send(JSON.stringify("message recieved"));
        });
    }

    render() {
        return ( <div className={"Live"}>
                {this.state.redirect}
                <form className={"lobby-join-form"} onSubmit={this.onJoinSubmit}>
                    <input className={"lobby-input"} type={"text"} placeholder={"Enter game code"} autoComplete={"off"}/>
                    <input className={"lobby-input"} type={"text"} placeholder={"Enter name"} autoComplete={"off"}/>
                    <input className={"lobby-submit"} type={"submit"} value={"Join Game"}/>
                </form>

                <form className={"lobby-create-form"} onSubmit={this.onLobbyCreateSubmit}>
                    <input className={"lobby-input"} type={"text"} placeholder={"Enter name"} autoComplete={"off"}/>
                    <input className={"lobby-submit"} type={"submit"} value={"Create New Game"}/>
                </form>

            </div>

        );
    }
}

export default Live;