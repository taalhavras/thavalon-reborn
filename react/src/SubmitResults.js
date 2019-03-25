import React, { Component } from 'react';
import "./css/Player.css";


// different types of game ending results
const results = ["Good Wins!", "Evil wins on missions!", "Evil wins by assassination!"];

/**
 * Models a game page, with links to each player.
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
        }).then(() => {
            // redirect to homepage
            window.location.href = "/";
        }).catch(error => {
            console.log(error);
        });
    };


    render() {
        return (<div className={"submitresults"}>
            <h1> Submit results for game {this.props.location.state.id} </h1>
                <ul>
                    {results.map(ele => {
                        return <li>
                            <button onClick={this.make_submitter(ele)}>
                                {ele}
                            </button>
                        </li>
                    })}
                </ul>
        </div>);
    }
}

export default SubmitResults;