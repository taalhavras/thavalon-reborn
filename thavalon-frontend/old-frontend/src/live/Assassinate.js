import React, {Component} from 'react';
import "../css/Proposal.scss"
import {socket} from "../index";

class Assassinate extends Component {

    constructor(props) {
        super(props);
        console.log("here2");
        this.state = {
            type: null,
            next: false,
            players: this.props.targets.map((ele => {
                return {name: ele, selected: false}
            }))
        }
    }


    next = (type) => {
        this.setState({type: type, next: true})

    };

    select = (ele) => {
        if (ele.selected) {
            ele.selected = false;
        } else {
            let selected = 0;
            this.state.players.forEach(ele => {
                if (ele.selected) {
                    selected++;
                }
            });
            if (selected === this.state.type) {
                return;
            } else {
                ele.selected = true;
            }

        }
        this.forceUpdate();

    };


    assasinate = () => {
        let type;
        const targets = [];
        switch (this.state.type) {
            case 0:
                type = "no_targets";
                break;
            case 1:
                type = "merlin";
                break;
            case 2:
                type = "lovers";
                break;

        }
        this.state.players.forEach(ele => {
            if (ele.selected) {
                targets.push(ele);
            }
        });

        socket.send(JSON.stringify({
            type: "ASSASSINATE_RESPONSE",
            assassination_type: type,
            targets: targets.map(ele => {return ele.name}),
            id: this.props.id,
            name: this.props.name


        }));
        this.props.hide(this);
    };

    render() {
        console.log("here3");


        return (
            <div className={"Assassinate pop-up"}>
                {this.state.next ?
                    <div>
                        {this.state.type === 1 ? <h2>You are assassinating a player as Merlin. Select your target</h2>
                            : <h2> You are assassinating Lovers. Select your targets</h2>}
                        <div className={"player-list"}>
                            {this.state.players.map(ele => {
                                const selected = ele.selected ? "proposal-player  selected" : "proposal-player";
                                return (
                                    <div className={"player-wrapper"}>
                                        <div className={selected} onClick={() => this.select(ele)}>
                                            <div className={"player-text"}> {ele.name} </div>
                                        </div>
                                    </div>)
                            })}

                        </div>
                        <button className={ "proposal-button assassinate-button"} onClick={this.assasinate}>Confirm</button>
                    </div> :
                    <div>
                        Good has passed three missions. You now have the chance to assassinate.

                        Which role would you like to assassinate?

                        <button className={" proposal-button assassinate-button"}
                                onClick={() => this.next(1)}>Merlin</button>
                        <button className={"proposal-button assassinate-button"} onClick={() => this.next(2)}>Lovers
                        </button>
                        <button className={"proposal-button assassinate-button"} onClick={() => this.next(0)}>No
                            Assassinable Targets
                        </button>

                    </div>
                   }

            </div>

        );
    }
}

export default Assassinate;
