import React, { Component } from 'react';
import "./css/Player.css";
import Link from "react-router-dom/es/Link";


// different types of game ending results
const results = ["Good Wins!", "Evil wins on missions!", "Evil wins by assassination!"];

/**
 * Shows different options for how games can end and sends results to server
 */
class SubmitResults extends Component {
    constructor(props) {
        super(props);
    }

    make_submitter = (result) => {
        return () => {
            this.submit_results(result);
        }
    };

    submit_results = (result) => {
        const id = this.props.location.state.id;
        const url = "/gameover/" + id;
        fetch(url, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',

            },
            body: JSON.stringify({
                result: result
            })
        }).catch(error => {
            console.log(error);
        });
    };


    render() {
        let count = 0;
        return (<div className={"submitresults"}>
            <h1> Submit results for game {this.props.location.state.id} </h1>
                <ul>
                    {results.map(ele => {
                        count ++;
                        return <li><Link key={count} onClick={this.make_submitter(ele)} to={{pathname: "/"}}>
                                <button className={"my_button, large_button"}>
                                    {ele}
                                </button>
                            </Link></li>
                    })}
                </ul>
        </div>);
    }
}

export default SubmitResults;