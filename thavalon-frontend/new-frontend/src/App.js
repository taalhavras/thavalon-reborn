import React from 'react';
import lefttrees from './assets/left-trees.svg';
import righttrees from './assets/right-trees.svg';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import Home from "./Home.js";
import './styles/App.scss';
import Player from './Player'
import DoNotOpen from './DoNotOpen.js';
import Game from './Game'
import Rules from "./Rules";



export const url = 'https://thavalon-api-qa.herokuapp.com';

/**
 * Main component for the program, which holds the routing information.
 *
 * Information is stored in the URL, using React Router, which allows
 * for refreshing without cookies.
 */

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = { rules: false }
  }

    /**
     * Routes the user to the correct component, storing information in the URL parameter
     * @returns {*}
     */

    toggleRules = () => {
        this.setState({ rules: !this.state.rules });
    };

    componentDidMount() {
        /* wakes uo the api */
        fetch(url, {
            method: "GET"
        }).catch(error => console.error(error));
    }

    render() {
        return (
            <Router className={"App"}>
                { this.state.rules? <Rules close={this.toggleRules} /> : null }

                    <Switch>
                    <Route exact path={"/"} component={Home}/>
                    <Route exact path={"/:id"} component={Game} />
                    <Route exact path={"/:id/donotopen"} component={DoNotOpen} />
                    <Route exact path={"/:id/:player"} component={Player} />

                </Switch>
                <button className={'rules-button'} onClick={this.toggleRules}> ? </button>
              <img src={lefttrees} className={'background-img'} id={"background-left"} alt={"Some trees"} />
              <img src={righttrees} className={'background-img'} id={"background-right"} alt={"Some trees"} />
            </Router>
    );
  }
}

export default App;
