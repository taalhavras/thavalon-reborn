import React, { Component } from 'react';
import "./css/Voting.scss"


class Voting extends Component {
    constructor(props) {
        super(props);

        this.state = {
            canFail: true,
            canReverse: true,
            passSelected: false,
            failSelected: false,
            reverseSelected: false,
        }
    }

    togglePass = () => {
        this.setState({passSelected: !this.state.passSelected, failSelected: false, reverseSelected: false})
    };

    toggleFail = () => {
        this.setState({failSelected: !this.state.failSelected, passSelected: false, reverseSelected: false})
    };

    toggleReverse = () => {
        this.setState({reverseSelected: !this.state.reverseSelected, passSelected: false, failSelected: false})
    };
    render() {

        let passClasses = this.state.passSelected ? "selected": "";
        passClasses = "card pass " + passClasses;

        let failClasses = this.state.failSelected ? "selected": "";
        failClasses = "card fail " + failClasses;

        let reverseClasses = this.state.reverseSelected ? "selected": "";
        reverseClasses = "card reverse " + reverseClasses;
        return (

            <div className={"Voting"}>
                <div className={"card-wrapper"}>
                <div className={passClasses} onClick={this.togglePass} >
                    <div className={"card-content"}> Pass </div>
                </div>
                {this.state.canFail ?
                    <div className={failClasses} onClick={this.toggleFail}>

                        <div className={"card-content"}> Fail </div>

                    </div> : null}
                {this.state.canReverse ?
                    <div className={reverseClasses} onClick={this.toggleReverse}>
                        <div className={"card-content"}> Reverse </div>

                    </div> : null}
                </div>

                <button id={"confirm"} className={"large-button"}>Confirm</button>
            </div>

        );
    }
}

export default Voting;