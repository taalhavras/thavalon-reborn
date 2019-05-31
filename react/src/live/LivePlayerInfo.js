import React, { Component } from 'react';
import '../css/Board.scss';


class LivePlayerInfo extends Component {

    constructor(props) {
        super(props);
        this.state =  {
            name: this.props.info.name,
            role: this.props.info.role,
            role_info: this.props.info.role_info,
            description: this.props.info.description
        };
    }

    render() {
        return ( <div className={"LivePlayerInfo pop-up"}>
                <button className={"close-button"} onClick={() => this.props.hide(this)}>
                    <i className="fas fa-times"></i></button>
                <h2 className={"player-title"}>You are {this.state.role}</h2>
                <div className={"description"}>
                    {this.state.description}
                    {this.state.role_info}
                </div>
                <ul>
                </ul>
            </div>

        );
    }
}

export default LivePlayerInfo;
