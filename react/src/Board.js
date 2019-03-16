import React, { Component } from 'react';
import './css/App.css';
import './css/Player.css';
import Mission from "./Mission";

/**
 * Models a boards
 */
class Board extends Component {
    // constructor(props) {
    //     super(props);
    //     let board = [];
    //     console.log(this.props.location.state);
    //     switch (this.props.location.state.num) {
    //         case 5:
    //             board = [2, 3, 3, 3, 3];
    //             break;
    //         case 7:
    //             board = [2, 3, 3, 4, 4];
    //             break;
    //         case 8:
    //             board = [3, 4, 4, 5, 5];
    //             break;
    //         case 10:
    //             board = [3, 4, 4, 5, 5];
    //             break;
    //         default:
    //             board = [0];
    //             break;
    //     }
    //     this.state = {board: board};
    // }
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