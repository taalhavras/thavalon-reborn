import React from "react";
import { Redirect } from 'react-router-dom';
import { url } from './App'

/**
 * Models the NewGame fields.
 */
class EndGame extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            start: true,
            redirect: null
        };
    }


    /**
     * Makes a post request to the url to start a game
     * @param obj
     */
    sendGamePost = () => {
        fetch( url + '/names', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',

            },
            body: JSON.stringify({names: this.props.names})
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
                document.location.reload();
            }

        }).catch(error => {
            console.log(error);
        });
    };

    render() {
        return (
            <div className={"EndGame"}>
                {this.state.redirect}
                {this.state.first ?
                    <div>
                        <h3>Game Result</h3>
                        <button className={'large-button'} onClick={() =>
                            this.setState({first: false})}>Good Wins</button>
                        <button className={'large-button'}
                                onClick={() => this.setState({first: false})}>Evil Wins on Missions</button>
                        <button className={'large-button'}
                                onClick={() => { this.setState({first: false}); document.location.reload();
                                }}>Evil Wins on Assassination</button>

                    </div> :
                    <div>
                        <h2> Game Over </h2>
                        <button className={'button-large'} onClick={this.sendGamePost}> Reroll Game</button>
                        <button className={'button-large'} onClick={() =>
                            this.setState({redirect: <Redirect to={{ pathname: ""}}/>})}>
                            Go Home </button>


                    </div>

                }
            </div>
        )
    }

}

export default EndGame;