import React from "react";
import './styles/Home.scss'
import NewGame from "./NewGame";
import JoinGame from "./JoinGame";

class Home extends React.Component {
    constructor(props) {
        super(props);
        this.base =
            <div className={"home-buttons"}>
                <button className={"button-large"} onClick={() => this.setComponent(<NewGame back={this.goHome}
                                                                                             setComponent={this.setComponent}

                />)}> New Game</button>
                <button className={"button-large"}  onClick={() => this.setComponent(<JoinGame back={this.goHome}/>)} >Join Game</button>
             </div>;
        this.state = {
            currComponent: this.base
        }

    }
    goHome = () => this.setState({currComponent: this.base});
    setComponent = (component) => this.setState({currComponent: component});


    render() {
        return (
            <div className={"Home"}>
                <h1>thavalon</h1>
                {this.state.currComponent}

            </div>
        )
    }

}

export default Home;