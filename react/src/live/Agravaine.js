import React, {Component} from 'react';
import "../css/Proposal.scss"
import {socket} from "../index";

class Agravaine extends Component {

    constructor(props) {
        super(props);
        this.state = {}
    }

    decide = (choice) => {
        socket.send(JSON.stringify({
                type: "AGRAVAINE_RESPONSE",
                choice: choice,
                id: this.props.match.params.id
            })
        );
        this.props.hide(this);
    };


    render() {

        return (
            <div className={"Agravaine pop-up"}>
                <h2 className={"agravaine-h2"}> Would you like to declare as Agravaine to cause this mission to
                    fail? </h2>
                <button className={"proposal-button"} onClick={() => this.decide(true)}>Yes</button>
                <button className={"proposal-button"} onClick={() => this.decide(false)}>No</button>

            </div>

        );
    }
}

export default Agravaine;
