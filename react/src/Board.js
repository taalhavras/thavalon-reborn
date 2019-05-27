import React, { Component } from 'react';
import './css/Board.scss';

import Mission from "./Mission";
import PlayerList from "./PlayerList";
import LivePlayerInfo from "./LivePlayerInfo";
import Proposal from "./Proposal";
import ProposalVoting from "./ProposalVoting";
import Voting from "./Voting";
/**
 * Models a boards
 */
class Board extends Component {
    constructor(props) {
        super(props);
        let missions = [2, 3, 2, 3, 3];
        // switch (this.props.location.state.numPlayers) {
        //     case 5:
        //         missions = [2, 3, 2, 3, 3];
        //         break;
        //     case 7:
        //         missions = [2, 3, 3, 4, 4];
        //         break;
        //     case 8: case 10:
        //         missions = [3, 4, 4, 5, 5];
        //         break;
        //     default:
        //         break;
        // }
        this.state = {
            missions: missions.map(ele => {return {
                num: ele,
                passed: 0,
            }
            }),
            options: ["unsent", "passed", "failed"],
            showInfo: false,
            showMission: false,
            showProposal: false,
            showVoting: false,
            showProposalVoting: false,
            game: [],
            redirect: ""
        }

    }

    // retrieves the player info for this game
    render_game = () => {
        const id = this.props.match.params.id;
        console.log("Get Game Info");
        const url = "/game/info/" + id;
        fetch(url, {
            method: "GET"
        }).then(response => {
                return response.json();
            }) .then (data => {
            console.log("Response");
            console.log(data);
            if(data.length === 0) {
                // invalid id, redirect to homepage
                //  this.setState({redirect: <Redirect to={{ pathname: "/" }} />});
                console.log("BAD!");
            } else {
                // found id, just do lookup for this specific player
                this.setState({game: data});
                return data;
            }

        }).catch(error => {
            console.log(error);
        });
    };



    onClickHandler = (ele) => {
        ele.passed = ele.passed === 3 ? 1 : ele.passed + 1;
        console.log(ele);
        this.forceUpdate();
    };

    toggleInfo = () => {
        this.setState({showInfo: !this.state.showInfo});
    };

    toggleProposal = () => {
        this.setState({showProposal: !this.state.showProposal});
    };

    toggleMission = () => {
        this.setState({showMission: !this.state.showMission});
    };

    toggleVoting = () => {
        this.setState({showVoting: !this.state.showVoting});
    };

    toggleProposalVoting = () => {

        this.setState({showProposalVoting: !this.state.showProposalVoting});

    };

    componentWillMount() {
        this.render_game();
    }

    render() {
        {this.state.redirect}
        const mission = <Mission
            close={this.toggleMission}
            num={1}
            onMission={["Grace", "Raghu", "Philip"]}
            result={"Passed"}
            cardsPlayed={["Fail", "Pass", "Reverse"]}
            proposedBy={"Grace"}
            votedFor={["Grace", "Raghu", "Kevin"]}
            votedAgainst={["Philip", "May"]}
        />;
        return (
            <div className={"Board"}>
                {this.state.showMission ? mission: null}
                {this.state.showProposal ? <Proposal/>: null}
                {this.state.showInfo ? <LivePlayerInfo close={this.toggleInfo}/> : null}
                {this.state.showProposalVoting ? <ProposalVoting/> : null}
                {this.state.showVoting ? <Voting/> : null}


                <div className={"name"} onClick={this.toggleInfo}>Grace</div>
                <div className={"proposal-order"}>
                    <PlayerList/>
                    <button className={"propose-button large-button"} onClick={this.toggleProposal}>Propose</button>
                    <button className={"propose-button large-button"} onClick={this.toggleVoting}>Show Voting</button>
                    <button className={"propose-button large-button"} onClick={this.toggleProposalVoting}>Show Proposal Voting</button>

                </div>
                <div className={"missions"}>
                {this.state.missions.map(ele => {
                    const classname = this.state.options[ele.passed] + " mission" ;
                    console.log(classname);
                    return (<div className={classname}
                    onClick={this.toggleMission}>
                        <div className={"num"}> {ele.num}</div>
                    </div>);
                })}
                </div>
            </div>
        );
    }
}

export default Board;