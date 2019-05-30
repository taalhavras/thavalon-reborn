import React, {Component} from 'react';
import "../css/Proposal.scss"
import {socket} from "../index.js";

/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Proposal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            players: this.props.players.map(ele => {
                return {name: ele, selected: false}
            }),
            selected: [],
            error: ""
        }
    }

    /**
     * Deselects a player if they are already selected, otherwise seletcts them if possible
     * @param ele
     */
    toggleSelected = (ele) => {
        if (ele.selected) {
            ele.selected = false;
        } else {
            let selected = 0;
            this.state.players.forEach(ele => {
                if (ele.selected) {
                    selected++;
                }
            });
            if (selected === this.props.num) {
                return;
            } else {
                ele.selected = true;
            }

        }
        this.forceUpdate();
    };


    sendProposal = () => {

        const proposal = this.state.players.filter(ele => ele.selected).map(ele => ele.name);
        if (proposal.length !== this.props.num) {
            this.setState({error: "Not enough players selected"});
            return;
        }
        const type = this.props.missionOne ? "MISSION_ONE_PROPOSAL_RESPONSE" : "MISSION_PROPOSAL_RESPONSE";

        socket.send(JSON.stringify({type: type, proposal: proposal,  id: this.props.match.params.id, name:this.props.name
        }));
        this.props.hide(this);
    };

    render() {
        return (
            <div className={"Proposal pop-up"}>
                <div className={"player-list"}>
                    {this.state.players.map(ele => {
                        const selected = ele.selected ? "selected" : "";
                        return (
                            <div className={"player-wrapper"}>
                                <div className={"proposal-player " + selected} onClick={() => this.toggleSelected(ele)}>
                                    <div className={"player-text"}> {ele.name} </div>
                                </div>
                            </div>)
                    })}
                </div>
                <div className={"error"}>{this.state.error}</div>
                <button className={"large-button"} id={"proposal-confirm"} onClick={this.sendProposal}>Confirm</button>
            </div>

        );
    }
}

export default Proposal;