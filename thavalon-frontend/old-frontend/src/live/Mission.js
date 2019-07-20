import React, { Component } from 'react';
import "../css/Board.scss"
import {csv} from "./ProposalVoting";

class Mission extends Component {

    constructor(props) {
        super(props);

    }

    render() {
        return (
            <div className={"Mission pop-up"}>
                <button className={"close-button"} onClick={this.props.hide}><i className="fas fa-times"></i> </button>
                <h2>Mission {this.props.num}</h2>
                <div className={"mission-info"}>On mission: {csv(this.props.onMission)}</div>
                <div className={"mission-info"}>Result: {this.props.result} </div>
                <div className={"mission-info"}>Cards played: {csv(this.props.cardsPlayed)}</div>

                <div className={"mission-info"}>Proposed by: {this.props.proposedBy} </div>
                <div className={"mission-info"}>Voted For: {csv(JSON.parse(this.props.votedFor))} </div>
                <div className={"mission-info"}>Voted Against: {csv(this.props.votedAgainst)} </div>


            </div>
        );
    }
}


export default Mission;