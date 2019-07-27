import React from "react";
import { Redirect } from 'react-router-dom';
import { url } from './App'

/**
 * Models the NewGame fields.
 */
class NewGame extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            valid: false,
            names: [],
            redirect: null,
            error: '',
            customView: false
        }
    }

    /**
     * Validates that the input field contains 5, 7, 8, or 10 whitespace separated names
     * @param ele input field to validate
     */
    validateInputField = (event) => {
        console.log(event.target.value);
        const val = event.target.value;
        const regex = /^([\w\d]+)\s+([\w\d]+)\s+([\w\d]+)\s+([\w\d]+)\s+([\w\d]+)\s*([\w\d]+)?\s*([\w\d]+)?\s*([\w\d]+)?\s*([\w\d]+)?\s*$/;
        const match = regex.exec(val);
        console.log(val);
        if (match) {
            console.log("here");
            match.shift();
            const strings = match.filter(ele => typeof ele === 'string');
            if ([5, 7, 8, 10].includes(strings.length) && (new Set(strings)).size === strings.length) {
                this.setState({valid: true, names: strings});
                return;
            }

        }
        this.setState({valid: false});

    };

    /**
     * Makes a post request to the url to start a game
     * @param obj
     */
    sendGamePost = () => {
        fetch(url + '/names', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',

            },
            body: JSON.stringify({names: this.state.names})
        }).then(response => {
            return response.json();
        }).then(data => {
            if(data.hasOwnProperty("error")) {
                // error, report to user
                this.setState({error: data["error"]});
            } else {
                // no error
                const redirect = <Redirect to={{ pathname: "/" + data["id"]}} />;
                this.setState({redirect: redirect});
            }

        }).catch(error => {
            console.log(error);
        });
    };

    render() {
        const inputClass = this.state.valid ? 'valid' : 'invalid';
        const buttonClass = this.state.valid ? 'button-large' : 'button-large invalid-button';
        return (
            <div className={"NewGame"}>
                {this.state.redirect}
                <button className={'back-button'} onClick={this.props.back}><i className="material-icons">
                    arrow_back </i>
                </button>
                <div className={"home-buttons"}>
                    <div className={'input-wrapper'}>
                    <input
                        id={"new-game-input"}
                        className={inputClass}
                        placeholder={'Enter player names'}
                        type={'text'}
                        onChange={this.validateInputField}/>
                    {this.state.valid ?
                        <i className="material-icons validate-icon valid-icon">done_alt</i> :
                        <i className="material-icons validate-icon invalid-icon">clear_alt</i>}
                    </div>
                    <button className={buttonClass} onClick={this.sendGamePost}>
                        Start Game
                    </button>
                </div>
            </div>
        )
    }

}

export default NewGame;