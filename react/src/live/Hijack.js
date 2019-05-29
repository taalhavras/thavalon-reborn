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
                    choice: choice,
                    id: this.props.match.params.id

                })
            );
            this.props.hide(this);
        } else {
            this.setState({next: true});
        }
    };

    select = (ele) => {
        ele.selected = true;

        this.state.players.forEach(el => {
            if (el !== ele) {
                el.selected = false
            }
        });

        this.forceUpdate();
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
                        <div className={"players-wrapper"}>
                            {this.state.players.map(ele => {
                            const selected = ele.selected ? "selected" : "";
                            return (
                                <div className={" player-wrapper"}>
                                    <div className={"proposal-player " + selected}
                                         onClick={() => this.select(ele)}>
                                        <div className={"player-text"}> {ele.name} </div>
                                    </div>
                                </div>)
                        })}

                        </div>
                        <button className={"large-button proposal-button"} onClick={this.hijack}>Confirm</button>

                    </div> :
                    <div className={"proposal-wrapper"}>
                        <h2> You have the Hijack ability. Would you like to remove a player from this mission? </h2>
                        <div>
                            <button className={"proposal-button"} onClick={() => this.decide(true)}>Yes</button>
                            <button className={"proposal-button"} onClick={() => this.decide(false)}>No</button>
                        </div>
                    </div>
                }


            </div>

        );
    }
}

export default Hijack;
