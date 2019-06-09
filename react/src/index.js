import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import "./css/index.scss";
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import Game from './Game.js';
import Board from './live/Board.js';
import DoNotOpen from './DoNotOpen.js';
import Lobby from './live/Lobby.js';
import Player from './Player.js';
import Voting from './live/Voting.js';


import * as serviceWorker from './serviceWorker';
import SubmitResults from "./SubmitResults";
import Proposal from "./live/Proposal";
import ProposalVoting from "./live/ProposalVoting";
import PlayerList from "./live/PlayerList";

let socket  = new WebSocket("ws://thavalon.com/socket");
console.log(socket);

ReactDOM.render(<BrowserRouter>
    <Switch>
        <Route exact path='/' component={App} />
        <Route exact path='/live/:id' component={Lobby} />
        <Route exact path='/board' component={Board} />

        <Route exact path='/voting' component={Voting} />
        <Route exact path='/proposal' component={Proposal} />
        <Route exact path='/proposalvoting' component={ProposalVoting} />
        <Route exact path='/playerlist' component={PlayerList} />

        <Route exact path='/submitresults' component={SubmitResults}/>
        <Route exact path='/:id' component={Game} />
        <Route exact path='/:id/game/donotopen' component={DoNotOpen} />

        <Route exact path='/:id/board' component={Board} />
        <Route exact path='/:id/:name' component={Player} />


    </Switch>
</BrowserRouter>, document.getElementById('root'));

export {socket};

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();


