import React from "react";

class Home extends React.Component {
    constructor(props) {
        super(props);

    }

    render() {
        return (
            <div className={"Home"}>
                <h1>thavalon</h1>
                <div className={"home-buttons"}>
                <button className={"large-button"}>New Game</button>
                <button className={"large-button"}>Join Game</button>
                </div>
            </div>
        )
    }

}

export default Home;