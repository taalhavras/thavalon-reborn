import React, {Component} from 'react';
import {csv} from "./ProposalVoting"
import "../css/Proposal.scss"
import {socket} from "../index";

/**
 * Deals with voting on missions for mission one.
 */
class MissionProposalResult extends Component {

    constructor(props) {
        super(props);
        this.state = {}
    }

    confirm = () => {
        socket.send(JSON.stringify({type: "MISSION_PROPOSAL_RESULT_RESPONSE"}));
        this.props.hide(this);
    };

    render() {

        return (
            <div className={"MissionProposalResult pop-up"}>
                <div>{this.props.proposed_by}'s mission is {this.props.sent ? "going" : "not going"}</div>
                <div>Upvotes: {csv(this.props.votedFor)}</div>
                <div>Downvotes: {csv(this.props.votedAgainst)}</div>
                {this.props.hijacked ? <div>Hijacked by {this.props.hijackedBy}. {this.props.hijackRemoved} was
                    removed from the mission</div> : null}

                <button className={"large-button proposal-button"} onClick={this.confirm}>Confirm</button>

            </div>

        );
    }
}

export default MissionProposalResult;
