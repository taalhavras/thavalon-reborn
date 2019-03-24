import React, { Component } from 'react';
import "./css/Player.css";

/**
 * Models a game page, with links to each player.
 */
class SubmitResults extends Component {
    constructor(props) {
        super(props);
        this.state = {
            results: ["Good Wins!", "Evil wins on missions!", "Evil wins by assassination!"]
        }
    }

    make_submitter = (result) => {
        return () => {
            this.submit_results(result);
        }
    };

    submit_results = (result) => {
        const id = this.props.id;
        console.log("Submit Game Results");
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
            <h1> Submit results for game {this.props.id} </h1>
                <ul>
                    {this.state.results.map(ele => {
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