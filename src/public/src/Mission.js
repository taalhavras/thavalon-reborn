import React, { Component } from 'react';
import './css/App.css';
import './css/Player.css';

/**
 * Models the player page. Expects three props: name, which is the name of the player, role, which is
 * the role the game has assigned the player, and role_info, which is the information and text associated
 * with the role.

 */
class Mission extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pass: null,
            color: "grey",
            text: this.props.num
        }
    }

    toggle = () => {
        console.log(this.state.pass);
     if (this.state.pass === null) {
         this.setState({pass: true, color: "green", text: "Pass"});
     } else if (this.state.pass === true) {
         this.setState({pass: false, color: "red", text: "Fail"});
     } else {
         this.setState({pass: null, color: "grey", text: this.props.num});

     }
    };
    render() {
        return (
            <div className={"Mission"}>
                <button style={{background: this.state.color}} onClick={this.toggle}>{this.state.text}</button>
            </div>

        );
    }
}

export default Mission;