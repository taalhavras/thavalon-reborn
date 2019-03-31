import React, { Component } from 'react';
import "./css/Player.css";

/**
 * Models a game page, with links to each player.
 */
class DoNotOpen extends Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false,
            text: "Show"
        }
    }

    /**
     * Parses the information array for each player.
     * @param ele
     * @returns {*[]}
     */
    parseInfo = (ele) => {
        const info = JSON.parse(ele);
        console.log(info);
        let res = [];
        // collect all information arrays into one 2d array
        Object.keys(info).forEach((key) => {
            res.push(info[key]);
        });
        console.log(res);

        return res.flat().map(ele => {
            return <div>{ele}</div>
            }
        );

    };

    open = () => {
        this.setState({open: !this.state.open, text: this.state.open ? "Show" : "Hide"});
    };

    render() {
        return (<div className={"donotopen"}>
            <h1> Do Not Open </h1>
            <button className={"my_button, large_button"} id={"show_button"} onClick={this.open}>{this.state.text}</button>
            {this.state.open ?
                <ul className={"donotopen_list"}>
                    {this.props.location.state.game.map(ele => {
                        return <li><span className={"name"}>{ele.name}</span> is {ele.role}, seeing: {this.parseInfo(ele.information)}</li>
                    })}
                </ul> : null
                }

        </div>);
    }
}

export default DoNotOpen;