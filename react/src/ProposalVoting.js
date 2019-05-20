import React, { Component } from 'react';
import "./css/Proposal.scss"

function csv(list) {
    let string = "";
    for (let i=0; i<list.length; i++) {
        string += list[i] + ", ";
    }
    return string.slice(0, string.length-2);
}
class ProposalVoting extends Component {

    constructor(props) {
        super(props);
        this.state = {
            upSelected: false,
            downSelected: true,
            list: csv(["Grace", "Raghu", "Philip"])

        }
    }



    render() {
        return (

            <div className={"ProposalVoting pop-up" }>
                <div className={"proposal-list"}>
                    Voting on: {this.state.list}
                </div>
                <button className={"proposal-vote"}>
                    <i className="far fa-thumbs-up"></i>
                </button>
                <button className={"proposal-vote"}>
                    <i className="far fa-thumbs-down"></i>
                </button>


            </div>

        );
    }
}

export default ProposalVoting;
export {csv};