import React, { Component } from 'react';
import "./css/Player.css";
import { Link } from 'react-router-dom';

/**
 * Models a game page, with links to each player.
 */
class DoNotOpen extends Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false
        }
    }

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
            return <span>{ele} </span>
            }
        );

    };

    open = () => {
        this.setState({open: !this.state.open});
    };
    render() {
        return (<div className={"donotopen"}>
            <h1> Do Not Open </h1>
            <button className={"my_button, large_button"} id={"show_button"} onClick={this.open}>Show</button>
            {this.state.open ?
                <ul>
                    {this.props.location.state.game.map(ele => {
                        return <li>{ele.name}: {ele.role}, seeing: {this.parseInfo(ele.information)}</li>
                    })}
                </ul> : null
                }

        </div>);
    }
}

export default DoNotOpen;