import React, { Component } from 'react';
import { Link } from 'react-router-dom'
import { url } from './App'

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
        fetch(url + '/currentgames', {
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
            console.error(error);
        });
    }

    render() {
        let count = 0;
        return (<div className={"RecentGames"}>
            <h2>Recent Games</h2>
            <ul className={"recent-games-list"}>
                {this.state.gameIds.map(ele => {
                    count ++;
                    const link = "/" + ele;
                    return (
                        <li>
                        <Link key={count} className='game-link' to={{pathname: link}}>
                            Game {ele}
                        </Link>
                    </li>
                    );
                })}
            </ul>
        </div>);
    }
}

export default RecentGames;