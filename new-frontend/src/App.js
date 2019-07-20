import React from 'react';
import lefttrees from './assets/left-trees.svg';
import righttrees from './assets/right-trees.svg';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import Home from "./Home.js";


/**
 * Main component for the program, which holds the routing information.
 *
 * Information is stored in the URL, using React Router, which allows
 * for refreshing without cookies.
 */

class App extends React.Component {
  constructor(props) {
    super(props);
  }


  render() {
    return (

        <Router className={"App"}>
            <Switch>
                <Route exact path={"/"} component={Home}/>
            </Switch>
          <img src={lefttrees} id={"left-trees"} alt={"Some trees"} />
          <img src={righttrees} id={"right-trees"} alt={"Some trees"} />

        </Router>
    );
  }
}

export default App;
