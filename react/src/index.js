import React from 'react';
import ReactDOM from 'react-dom';
import './css/index.css';
import App from './App';
import "./css/styles.css";
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import Game from './Game.js';
import Board from './Board.js';
import DoNotOpen from './DoNotOpen.js';

import Lobby from './Live.js';

import Player from './Player.js';

import * as serviceWorker from './serviceWorker';
import SubmitResults from "./SubmitResults";

let socket  = new WebSocket("ws://localhost:4444/socket");
console.log(socket);

ReactDOM.render(<BrowserRouter>
    <Switch>
        <Route exact path='/' component={App} />
        <Route exact path='/live/:id' component={Lobby} />

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


