import React, { Component } from 'react';
import "../css/Board.scss"

class PlayerList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            players: this.props.players.map(ele => {return {name: ele, selected: false}})
            , curr: 0
        };
        console.log(this.state);
    }

    componentWillReceiveProps(nextProps, nextContext) {
        console.log("player list will recieve props");
        console.log(nextProps);
        console.log(nextContext);
        this.setState({
            players: nextProps.players.map(ele => {
                return {name: ele, selected: false}
            })
        });
        if (this.state.players.length > 0) {
            let players = this.state.players;
            let newPlayer = {name: players[0].name, selected: true};
            players.splice(0, 1, newPlayer);
            this.setState({curr: 0, players: players});



            // this.next();
        }


        this.forceUpdate();

    }

    next = () => {
        console.log("here");
        console.log(this.state);

        let players = this.state.players;
        let currPlayer = players[this.state.curr];

        let newPlayer = {name: currPlayer.name, selected: false};

        players.splice(this.state.curr, 1, newPlayer);

        const curr = this.state.curr === players.length - 1 ? 0 : this.state.curr + 1;
        newPlayer = {name: players[curr].name, selected: true};
        players.splice(curr, 1, newPlayer);
        this.setState({curr: curr, players: players});

    };

    render() {
        return (
            <div className={"PlayerList"}>
                <div className={"player-list-ele"}> Proposal Order: </div>
                    {this.state.players.map(ele => {
                    const selected = ele.selected ? "selected-list" : "";
                    return (
                        <div className={"player-list-ele " + selected}>
                            {ele.name}
                        </div>)

                })}
            </div>

        );
    }
}


export default PlayerList;