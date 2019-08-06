import React from "react";
import './styles/Game.scss';
import { url } from './App.js';
import RecentGames from "./RecentGames";
/**
 * Models the Join Game fields.
 */
class JoinGame extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            valid: false
        }

    }

    /**
     * Validates that the input field contains a valid game id
     * @param event input field to validate
     */
    validateInputField = (event) => {
        const id = event.target.value;
        if (id.length !== 4) {
            this.setState({valid: false});
            return;
        }
        const gameUrl = url + "/isGame/" + id;
        fetch(gameUrl, {
                method: "GET"
            }).then(response => {
                return response.json();
            }).then (data => {
                if (data) {
                    this.setState({valid: true})

                } else {
                    this.setState({valid: false});
                }
            });
        };


    render() {
        const inputClass = this.state.valid ? 'valid' : 'invalid';
        const buttonClass = this.state.valid ? 'button-large' : 'button-large invalid-button';
        return (
            <div className={"JoinGame"}>
                <div className={"home-buttons"}>
                    <button className={'back-button'} onClick={this.props.back}><i className="material-icons">
                        arrow_back </i>
                    </button>
                    <div className={'input-wrapper'}>
                        <input
                            id={"join-game-input"}
                            className={inputClass}
                            placeholder={'Enter Game ID'}
                            type={'text'}
                            onChange={this.validateInputField}/>
                        {this.state.valid ?
                            <i className="material-icons validate-icon valid-icon">done_alt</i> :
                            <i className="material-icons validate-icon invalid-icon">clear_alt</i>}
                    </div>
                    <button className={buttonClass}>
                        Join Game
                    </button>
                </div>
                <RecentGames />
            </div>
        )
    }
}

export default JoinGame;