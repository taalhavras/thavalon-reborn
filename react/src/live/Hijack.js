import React, {Component} from 'react';
import "../css/Proposal.scss"
import {socket} from "../index";

class Hijack extends Component {

    constructor(props) {
        super(props);
        this.state = {
            next: false,
            players: this.props.players.map(ele => {
                return {name: ele, selected: false}
            })

        }
    }

    decide = (choice) => {

        if (!choice) {
            socket.send(JSON.stringify({
                    type: "HIJACK_RESPONSE",
                    choice: choice
                })
            );
            this.props.hide(this);
        } else {
            this.setState({next: true});
        }
    };

    toggleSelected = (ele) => {
        this.state.players.forEach(el => el.selected = false);
        ele.selected = true;

    };

    hijack = () => {
        const selected = this.state.players.filter(ele => ele.selected);
        if (selected.length !== 1) {
            return;
        }
        socket.send(JSON.stringify({
            type: "HIJACK_RESPONSE",
            choice: true,
            player: selected[0]
        }));
        this.props.hide(this);


    };

    render() {

        return (
            <div className={"Hijack pop-up"}>

                {this.state.next ?
                    <div>
                        <h2> Choose a player to remove from the mission</h2>
                        {this.props.players.map(ele => {
                            const selected = ele.selected ? "selected" : "";
                            return (
                                <div className={"player-wrapper"}>
                                    <div className={"proposal-player " + selected}
                                         onClick={() => this.toggleSelected(ele)}>
                                        <div className={"player-text"}> {ele.name} </div>
                                    </div>
                                </div>)
                        })}
                        <button className={"large-button proposal-button"} onClick={this.hijack}>Confirm</button>
                    </div> :
                    <div>
                        You have the Hijack ability. Would you like to remove a player from this mission?
                        <button className={"proposal-button"} onClick={() => this.decide(true)}>Yes</button>
                        <button className={"proposal-buttom"} onClick={() => this.decide(false)}>No</button>
                    </div>
                }


            </div>

        );
    }
}

export default Hijack;
