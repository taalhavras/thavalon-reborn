import React from "react";
import './styles/Home.scss'
import NewGame from "./NewGame";
import JoinGame from "./JoinGame";

class Home extends React.Component {
    constructor(props) {
        super(props);
        this.base =
            <div className={"home-buttons"}>
                <button className={"button-large"} onClick={this.newGame}> New Game</button>
                <button className={"button-large"}  onClick={this.joinGame} >Join Game</button>
             </div>;
        this.state = {
            currComponent: this.base
        }

    }
    goHome = () => this.setState({currComponent: this.base});
    newGame = () => this.setState({currComponent: <NewGame back={this.goHome}/>});
    joinGame = () => this.setState({currComponent: <JoinGame back={this.goHome}/>});

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