import React, { Component } from 'react';
import './css/App.scss';
import { socket } from "./index.js"
import PlayerTag from "./PlayerTag";
import Options from "./Options";

/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Lobby extends Component {

    constructor(props) {
        super(props);
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
            names: JSON.parse(this.props.location.state.names),
            showOptions: false,
            roles: roles
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
                default:
                    break;
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

    optionsSubmit = () => {

    };

    render() {
        console.log("lobby");
        return (
            <div className={"Lobby"}>
                <Options options={this.state.roles} display={this.state.showOptions} submit={this.optionsSubmit}/>

                <h1>Lobby {this.props.match.params.id}</h1>
                {this.props.location.state.isCreator ?
                    <div id={"lobby-owner-view"}>
                        <h2>Players in Lobby</h2>
                        <div className={"player-tag-container"}>
                        {this.state.names.map(ele => {
                            return <PlayerTag name={ele} change={() => this.removePlayer(ele)}/>
                        })}
                        </div>
                        <button  className={"large-button lobby-button"} onClick={() => {this.setState({showOptions: !this.state.showOptions})}}>Game options</button>

                        <button className={"large-button lobby-button"}>Destroy Lobby</button>

                        <button className={"large-button lobby-button"}>Start Game</button>



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
            </div>

        );
    }
}

export default Lobby;