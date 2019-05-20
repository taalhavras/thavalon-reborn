import React, { Component } from 'react';
import "./css/Proposal.scss"

/**
 * Models the tag representing a player. Expects a String name prop, and a change handler.
 */
class Proposal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            players: [
                {name: "Grace", selected: false},  {name: "Kevin", selected: false},  {name: "Raghu", selected: false},
                {name: "May", selected: false},  {name: "Philip", selected: false}
                ],
            num: 3
        }
    }

    toggleSelected = (ele) => {
        ele.selected = !ele.selected;
        this.forceUpdate();
    };

    render() {
        return (
            <div className={"Proposal pop-up"}>
                <div className={"player-list"}>
                    {this.state.players.map(ele => {
                        const selected = ele.selected ? "selected" : "";
                        return (
                        <div className={"player-wrapper"}> <div className={"proposal-player " + selected} onClick={() => this.toggleSelected(ele)}>
                            <div className={"player-text"}> {ele.name} </div>
                        </div>
                        </div>)
                    })}
                </div>
                <button className={"large-button"} id={"proposal-confirm"}>Confirm</button>
            </div>

        );
    }
}

export default Proposal;