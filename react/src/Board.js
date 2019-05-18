import React, { Component } from 'react';
import './css/App.scss';
import './css/Player.scss';
import Mission from "./Mission";

/**
 * Models a boards
 */
class Board extends Component {
    render() {
        return (
            <div className={"Board"}>
                {/*Board*/}
                {/*{this.state.board.map(function(element) {*/}
                    {/*return <Mission num={element}/>;*/}
                {/*})}*/}
            </div>
        );
    }
}

export default Board;