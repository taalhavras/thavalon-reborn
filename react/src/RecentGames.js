import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import "./css/Player.css";


// number of games to fetch
const numGames = 5;

/**
 * Shows recently rolled games and allows users to navigate to them
 */
class RecentGames extends Component {
    constructor(props) {
        super(props);
        this.state = {
            gameIds: []
        }
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

    // fetch recent games from server
    componentDidMount() {
        const url = "/currentgames"
        fetch(url, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                numGames: numGames
            })
        }).then((response) => {
            // parse json
            return response.json();
        }).then((data) => {
            console.log(data);
            // set state
            this.setState({gameIds : data});
        }).catch(error => {
            console.log(error);
        });
    }

    render() {
        let count = 0;
        return (<div className={"recentgames"}>
                <ul>
                    {this.state.gameIds.map(ele => {
                        count ++;
                        const url = "/" + ele;
                        return <Link key={count} to={{pathname: url}}>
                            <button className={"my_button, large_button"}>{ele}</button>
                        </Link>
                    })}
                </ul>
        </div>);
    }
}

export default RecentGames;