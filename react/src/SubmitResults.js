import React, { Component } from 'react';
import "./css/Player.css";
import Link from "react-router-dom/es/Link";


// different types of game ending results
const results = ["Good Wins!", "Evil wins on missions!", "Evil wins by assassination!"];

const dontRecord = "Don't Record Results!";

/**
 * Shows different options for how games can end and sends results to server
 */
class SubmitResults extends Component {

    make_submitter = (result) => {
        return () => {
            this.submit_results(result, true);
        }
    };

    submit_results = (result, record) => {
        const id = this.props.location.state.id;
        const url = "/gameover/" + id;
        fetch(url, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',

            },
            body: JSON.stringify({
                result: result,
                record: record
            })
        }).catch(error => {
            console.log(error);
        });
    };

    dont_record_results = () => {
        this.submit_results(dontRecord, false);
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
                    <li>
                        <Link key={count + 1} onClick={this.dont_record_results} to={{pathname: "/"}}>
                            <button className={"my_button, large_button"}>
                                {dontRecord}
                            </button>
                        </Link>
                    </li>
                </ul>

        </div>);
    }
}

export default SubmitResults;