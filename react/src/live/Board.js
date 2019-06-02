import React, {Component} from 'react';
import '../css/Board.scss';
import Mission from "./Mission";
import PlayerList from "./PlayerList";
import LivePlayerInfo from "./LivePlayerInfo";
import Proposal from "./Proposal";
import ProposalVoting from "./ProposalVoting";
import Voting from "./Voting";
import {socket} from "../index.js";
import MissionOneProposalVoting from "./MissionOneProposalVoting";
import MissionProposalResult from "./MissionProposalResult";
import Agravaine from "./Agravaine";
import Hijack from "./Hijack";
import {Link} from "react-router-dom";
import Assassinate from "./Assassinate";
import {send} from "q";

/**
 * Models a boards
 */
class Board extends Component {
    constructor(props) {
        super(props);
        console.log(this.props.location.state.name);
        this.state = {
            missions: [],
            options: ["unsent", "passed", "failed"],
            popup: null,
            game: [],
            currMission: 0,
            redirect: "",
            players: [],
            proposal: null,
            currProposal: 0,
            assassinate: null,
            info: null,
            mission1: null,
            mission2: null,
        };

        this.playerList = React.createRef();

    }

    componentDidMount() {
        this.renderGame();
    }

    //sets up socket listeners
    setupSocket = () => {

        socket.addEventListener("message", (message) => {
            console.log("in board event handler");
            console.log(message.data);
            const parsed = JSON.parse(message.data);
            switch (parsed.type) {
                case "MISSION_ONE_PROPOSAL":
                    this.setState({
                        proposal: <Proposal
                            id={this.props.match.params.id}
                            name={this.props.location.state.name}
                            players={this.state.players}
                            missionOne={true}
                            num={this.state.missions[0].num}
                            hide={this.hide}
                            close={this.closeProposal}/>
                    });
                    break;
                case "MISSION_ONE_VOTING":
                    console.log(parsed);
                    this.setState({
                        popup: <MissionOneProposalVoting
                            id={this.props.match.params.id}
                            name={this.props.location.state.name}
                            firstProposal={parsed.first_proposal}
                            secondProposal={parsed.second_proposal}
                            hide={this.hide}
                        />
                    });
                    break;
                case "MISSION_ONE_VOTING_RESULT":
                    this.state.missions[0].votedFor = parsed.voted_sent;
                    this.state.missions[0].proposedBy = parsed.proposed_by;
                    console.log(parsed.proposed_by);
                    this.setState({

                        popup:
                            <MissionProposalResult
                                name={this.props.location.state.name}
                                sent={parsed.sent}
                                votedFor={parsed.voted_sent}
                                votedAgainst={parsed.voted_not_sent}
                                notSent={parsed.not_sent}
                                hide={this.hide}
                                id={this.props.match.params.id}

                            />
                    });
                    this.setState(
                        {
                            currMission: 1,
                        });
                    break;
                case "MISSION_PROPOSAL":
                    this.setState({
                        proposal:
                            <Proposal
                                id={this.props.match.params.id}
                                players={this.state.players}
                                name={this.props.location.state.name}
                                missionOne={false}
                                num={this.state.missions[this.state.currMission].num}
                                hide={this.hide}
                                close={this.closeProposal}
                            />
                    });
                    break;
                case "MISSION_PROPOSAL_RESULT":
                    const hijacked = parsed.hasOwnProperty("hijack");
                    let hijackedBy = "";
                    let hijackRemoved = "";
                    if (hijacked) {
                        hijackedBy = parsed.hijacked_by;
                        hijackRemoved = parsed.hijack_removed;
                    }
                    if (this.playerList.current) {
                        this.playerList.current.next();
                    }
                    this.state.missions[parsed.num].votedFor = parsed.voted_for;
                    this.state.missions[parsed.num].proposedBy = parsed.proposed_by;

                    this.setState({

                        popup:
                            <MissionProposalResult

                                name={this.props.location.state.name}
                                sent={parsed.sent}
                                votedFor={parsed.voted_for}
                                votedAgainst={this.state.players.filter(ele => !parsed.voted_for.contains(ele))}
                                hide={this.hide}
                                hijacked={hijacked}
                                hijackedBy={hijackedBy}
                                hijackRemoved={hijackRemoved}

                            />

                    });
                    break;
                case "MISSION_VOTING":
                    this.setState({popup: <ProposalVoting id={this.props.match.params.id} name={this.props.location.state.name}
                                                                                     players={parsed.proposal} hide={this.hide}/>});
                    break;
                case "PLAY_CARD":
                    console.log("play card");
                    const cards = JSON.parse(parsed.cards);
                    console.log(cards);
                    console.log(typeof cards);
                    this.setState({
                        popup: <Voting
                            id={this.props.match.params.id}
                            name={this.props.location.state.name}
                            canReverse={cards.includes("R")}
                            canFail={cards.includes("F")}
                            hide={this.hide}/>
                    });
                    break;
                case "MISSION_RESULT":
                    const missions = this.state.missions;
                    missions[parsed.num-1].passed = (parsed.result) ? 1 : 2;
                    const cardsPlayed = JSON.parse(parsed.cards);
                    missions[parsed.num-1].mission = <Mission

                        hide={this.hide}
                        num={parsed.num}
                        onMission={[parsed.players-1]}
                        result={(parsed.result === "P") ? "Passed" : "Failed"}
                        cardsPlayed={cardsPlayed.map(ele => {
                            switch (ele) {
                                case "P":
                                    return "Pass";
                                case "F":
                                    return "Fail";
                                case "R":
                                    return "Reverse";
                                default:
                                    return "";
                            }
                        })}
                        proposedBy={missions[parsed.num-1].proposedBy}
                        votedFor={missions[parsed.num-1].votedFor}
                        votedAgainst={this.state.players.filter(ele => !missions[parsed.num-1].votedFor.includes(ele))}
                    />;
                    console.log(missions);
                    this.setState(
                        {
                            currMission: parsed.num,
                            missions: missions,
                            popup: missions[parsed.num - 1].mission
                        });
                    break;
                case "HIJACK":
                    this.setState({popup: <Hijack id={this.props.match.params.id} name={this.props.location.state.name}
                                                                              hide={this.hide}/>});
                    break;
                case "AGRAVAINE":
                    this.setState({popup: <Agravaine id={this.props.match.params.id} name={this.props.location.state.name}
                                                                                 hide={this.hide}/>});
                    break;
                case "ASSASSINATE":
                    this.setState({assassinate: <Assassinate id={this.props.match.params.id}  name={this.props.location.state.name}
                                                                                         targets={parsed.targets} hide={this.hide}/>});
                    break;
                case "GAME RESULTS":
                    const popup = <div className={"GameResults pop-up"}>
                        <h2>Game Results</h2>
                        {parsed.result};
                        <Link to={{path: "/"}}/>
                    </div>;
                    this.togglePopup(popup);
                    this.forceUpdate();
                    break;
                default:
                    break;
            }
        });

    };

    // retrieves the player info for this game
    renderGame = () => {
        const id = this.props.match.params.id;
        console.log("Get Game Info");
        const url = "/game/info/" + id;
        fetch(url, {
            method: "GET"
        }).then(response => {
            return response.json();
        }).then(data => {
            console.log(data);
            if (data.length === 0) {
                console.error("Error retrieving game data from server");
            } else {
                //determines state variables from data

                const players = data.map(ele => ele.name);
                let missions = [];
                switch (players.length) {
                    case 5:
                        missions = [2, 3, 2, 3, 3];
                        break;
                    case 7:
                        missions = [2, 3, 3, 4, 4];
                        break;
                    case 8:
                    case 10:
                        missions = [3, 4, 4, 5, 5];
                        break;
                    default:
                        break;
                }
                console.log(players);
                this.setState({
                    game: data,
                    players: players,
                    missions: missions.map(ele => {
                        return {
                            num: ele,
                            passed: 0,
                            mission: null
                        }
                    }),
                    info: data.filter(ele => ele.name === this.props.location.state.name)[0]
                });

                console.log(this.state);
                this.forceUpdate();

                this.setupSocket();
                 // once the handler is set up and we've gotten all our info we send ready message
                const msg = {
                    type: "READY",
                    id: this.props.match.params.id,
                    name: this.props.location.state.name
                };
                socket.send(JSON.stringify(msg));


                return data;
            }


        }).catch(error => {
            console.error(error);
        });


    };


    onClickHandler = (ele) => {
        ele.passed = ele.passed === 3 ? 1 : ele.passed + 1;
        console.log(ele);
        this.forceUpdate();
    };

    //only ever one popup at a time, stored as a JSX element in state
    togglePopup = (ele) => {
        console.log("Toggle Popup");
        console.log(ele);
        console.log(this.state.popup);

        if (this.state.popup !== null && this.state.popup.type === ele.type) {
            this.setState({popup: null});
        } else {
            this.setState({popup: ele});
        }
    };

    hide = () => {
        console.log("hide");
        this.setState({popup: null});

    };

    closeProposal = () => {
        this.setState({popup: null, proposal: null});

    };

    render() {
        console.log(this.state);

        return (

            <div className={"Board"}>
                {this.state.popup}
                {this.state.info ?
                    <div className={"name"} onClick={() => this.togglePopup(<LivePlayerInfo hide={this.hide}
                                                                                            info={this.state.info}/>)}>
                        {this.props.location.state.name}
                    </div> : null}

                <div className={"proposal-order"}>
                    <PlayerList players={this.state.players} ref={this.playerList}/>
                    {this.state.proposal ?
                        <button className={"propose-button large-button"}
                                onClick={() => this.togglePopup(this.state.proposal)}>
                            Propose
                        </button> : null}
                    {this.state.assassinate ?
                        <button className={"propose-button large-button"}
                                onClick={() => this.togglePopup(this.state.assassinate)}>
                            Assassinate
                        </button> : null}

                </div>
                <div className={"missions"}>
                    {this.state.missions.map(ele => {
                        const classname = this.state.options[ele.passed] + " mission";
                        console.log(classname);
                        return (<div className={classname}
                                     onClick={() => this.togglePopup(this.state.missions[ele.num].mission)}>
                            <div className={"num"}> {ele.num}</div>
                        </div>);
                    })}
                </div>
            </div>
        );
    }
}

export default Board;