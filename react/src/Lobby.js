import React, { Component } from 'react';
import './css/App.scss';
import { socket } from "./index.js"

/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Lobby extends Component {

    constructor(props) {
        super(props);
        console.log("lobby");

        this.state = {
            creator: this.props.creator,
            names : [this.props.creator.name]
        }
    }


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
                    this.setState({lobbyID: parsed.id});
                    break;
                case "LOBBY_JOINED":
                    console.log("lobby joined");
                    break;
                default:
                    console.log("message type not recognized");
            }

            // socket.send(JSON.stringify("message recieved"));
        });
    }

    removePlayer = (name) => {
        const names = this.state.names.filter(ele => ele !== name);
        this.setState({names: names});
        socket.send(
            JSON.stringify({
                type: "REMOVE_PLAYER",
                name: name
            })
        )
    };

    render() {
        console.log("lobby");
        return (
            <div className={"Lobby"}>
                <h1>Lobby {this.props.match.params.id}</h1>
                {this.props.isCreator ?
                    <div id={"lobby-owner-view"}>
                        {this.state.names.map(ele => {
                            return <div className={"live-player-tag"}> {ele}</div>
                        })}


                </div>:
                    <div id={"lobby-view"}>
                        {this.state.names.map(ele => {
                            return <div className={"live-player-tag"}> {ele}</div>
                        })}

                    </div>}
            </div>

        );
    }
}

export default Lobby;