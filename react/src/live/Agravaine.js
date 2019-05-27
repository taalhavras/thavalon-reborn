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
                choice: choice
            })
        );
        this.props.hide(this);
    };


    render() {

        return (
            <div className={"Agravaine pop-up"}>
                Would you like to declare as Agravaine to cause this mission to fail?
                <button className={"proposal-button"} onClick={() => this.decide(true)}>Yes</button>
                <button className={"proposal-buttom"} onClick={() => this.decide(false)}>No</button>

            </div>

        );
    }
}

export default Agravaine;
