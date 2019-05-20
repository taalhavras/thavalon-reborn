import React, { Component } from 'react';
import './css/App.scss';
import Standard from "./Standard"
import Switch from 'react-switch'
import Live from "./Live.js";
import Rules from "./Rules";
import {Redirect} from "react-router-dom";

/**
 * The home screen for the game.
 */
class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isLiveGame: false,
            info: false
        }
    }

    info = () => {
        this.setState({info: !this.state.info})

    };

    /**
     * Renders the app.
     * @returns {*}
     */
  render() {
      return (
      <div className="App">
          <h1> THavalon </h1>
          <div className={"live"}>Live game?</div>
          <div className={"switch"}>
          <Switch onChange={() => {this.setState({isLiveGame: !this.state.isLiveGame})}} checked={this.state.isLiveGame}/>
          </div>
          {this.state.isLiveGame ? <Live/> : <Standard/>
        }
          <div className="info">
              {this.state.info ?
                  <div className={"rules_wrapper"}>
                      <button id={"exit_button"} onClick={this.info}><i className="fas fa-times"></i></button>
                      <div className={"rules"}>
                          <Rules/>
                      </div>
                  </div>: null}
              <button className={"info_button"} onClick={this.info}>?</button>
          </div>
      </div>



    );
  }
}

export default App;
