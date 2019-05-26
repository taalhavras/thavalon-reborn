import React, { Component } from 'react';
import './css/Board.scss';


class LivePlayerInfo extends Component {

    constructor(props) {
        super(props);
        this.state =  {
             name: this.props.name,
             role: this.props.role,
             role_info: this.props.role_info,
             description: this.props.description
        };
    }

    parseInfo = () => {
        const info = JSON.parse(this.props.info);
        console.log(info);
        let res = [];
        // collect all information arrays into one 2d array
        Object.keys(info).forEach((key) => {
            res.push(info[key]);
        });
        console.log(res);

        return res.flat();
    };

    render() {
        return ( <div className={"LivePlayerInfo pop-up"}>
                <button className={"close-button"} onClick={this.props.close}>
                    <i className="fas fa-times"></i></button>
                <h2 className={"player-title"}>You are {this.state.role}</h2>
                <div className={"description"}>
                    {this.state.description}
                    {this.parseInfo(this.state.role_info)}
                </div>
                <ul>
                </ul>
            </div>

        );
    }
}

export default LivePlayerInfo;
