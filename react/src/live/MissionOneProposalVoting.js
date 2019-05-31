import React, {Component} from 'react';
import {csv} from "./ProposalVoting"
import "../css/Proposal.scss"
import {socket} from "../index";

/**
 * Deals with voting on missions for mission one.
 */
class MissionOneProposalVoting extends Component {

    constructor(props) {
        super(props);
        this.state = {
            missionOneSelected: false,
            missionTwoSelected: false,
        }
    }

    selectFirstProposal = () => {
        this.setState({missionOneSelected: true, missionTwoSelected: false});

    };

    selectSecondProposal = () => {
        this.setState({missionOneSelected: false, missionTwoSelected: true});

    };

    vote = () => {
        let vote = "";
        if (this.state.missionOneSelected) {
            vote = "upvote";
        } else if (this.state.missionTwoSelected) {
            vote = "downvote";
        } else {
            return;
        }
        socket.send(JSON.stringify({type: "MISSION_ONE_VOTING_RESPONSE", vote: vote, id: this.props.id, name:this.props.name
        }));
        this.props.hide(this);
    };


    render() {
        const firstClasses = this.state.missionOneSelected ? "mission-one-mission selected" : "mission-one-mission";
        const secondClasses = this.state.missionTwoSelected ? "mission-one-mission selected" : "mission-one-mission";

        return (
            <div className={"MissionOneProposalVoting pop-up"}>
                <h2>Mission One</h2>
                <div className={"mission-wrapper"}>
                    <div className={firstClasses} onClick={this.selectFirstProposal}>
                        First Proposal: {csv(JSON.parse(this.props.firstProposal))}
                    </div>
                    <div className={secondClasses} onClick={this.selectSecondProposal}>
                        Second Proposal: {csv(JSON.parse(this.props.secondProposal))}
                    </div>
                </div>
                <button className={"large-button proposal-button"} onClick={this.vote}>Vote</button>

            </div>

        );
    }
}

export default MissionOneProposalVoting;
