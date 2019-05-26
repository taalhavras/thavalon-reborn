import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import "./css/Player.scss";


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

    // fetch recent games from server
    componentDidMount() {
        const url = "/currentgames";
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
                <ul className={"recentgameslist"}>
                    {this.state.gameIds.map(ele => {
                        count ++;
                        const url = "/" + ele;
                        return <li><Link key={count} to={{pathname: url}}>
                            <button className={"large-button"}>
                                {ele}
                            </button>
                        </Link></li>
                    })}
                </ul>
        </div>);
    }
}

export default RecentGames;