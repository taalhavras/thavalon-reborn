import React, { Component } from 'react';
import '../css/Board.scss';


class LivePlayerInfo extends Component {

    constructor(props) {
        super(props);
        console.log(this.props.info);
        this.state =  {
            name: this.props.info.name,
            role: this.props.info.role,
            role_info: this.props.info.information,
            description: this.props.info.description
        };
    }

    parseInfo = (info) => {
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
                <button className={"close-button"} onClick={() => this.props.hide(this)}>
                    <i className="fas fa-times"></i></button>
                <h2 className={"player-title"}>You are {this.state.role}</h2>
                <div className={"description"}>
                    {this.state.description}
                    <div>

                <div className={"info"}>
                    {this.parseInfo(JSON.parse(this.state.role_info))}
                </div>
                <ul>
                </ul>
            </div>

        );
    }
}

export default LivePlayerInfo;
