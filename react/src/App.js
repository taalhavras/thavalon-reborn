import React, { Component } from 'react';
import './css/App.css';
import {Redirect} from 'react-router-dom';
import "./css/styles.css";
import "./PlayerTag";
import Rules from "./Rules";
import "./css/PlayerTag.css";
import PlayerTag from "./PlayerTag";
import Options from "./Options";
import RecentGames from "./RecentGames";

/**
 * The home screen for the game.
 */
class App extends Component {

    /**
     * Constructor for the lobby. Initializes state objects,
     * including roles, and truth/false values for hidden screens.
     */
    constructor(props) {
        super(props);
        const roles =  [
            {key: "Arthur", value: true},
            {key: "Galahad", value: false},
            {key: "Lancelot", value: true},
            {key: "Percival", value: true},
            {key: "Lone Percival", value: false},
            {key: "Guinevere", value: true},
            {key: "Merlin", value: true},
            {key: "Titania", value: true},
            {key: "Nimue", value: false},
            {key: "Gawain", value: false},
            {key: "Lovers", value: true},
            {key: "Lone Lovers", value: false},
            {key: "Mordred", value: true},
            {key: "Morgana", value: true},
            {key: "Maelagant", value: true},
            {key: "Oberon", value: true},
            {key: "Agravaine", value: true},
            {key: "Colgrevance", value: true},
            {key: "Duplicate Roles", value: false}


        ];

        this.state = {
            input: false,
            info: false,
            players: [],
            names: [],
            currId: "NULL",
            game: [],
            options: false,
            join: false,
            join_redirect: "",
            join_input: (<input type="text"  className={"input_ele"} id ={"join"} placeholder={"Enter Game ID"} />),
            roles: roles,
            switches: [],
            redirect: "",
            player_key: 0,
            join_error: "",
            error: ""
        };

    }

    /**
     * Shows the options panel.
     */
    options = () => {

        this.setState({options: !this.state.options})


    };

    /**
     * Hides the options panel and collects the data to be stored in state.
     * Passed to the options comoponent as a prop.
     * @param event
     */
    optionsSubmit = (event) => {
        event.preventDefault();
        let options = this.state.roles;
        for (let i = 0; i < event.target.length-1; i++) {

            options[i] = {key: event.target[i].name, value: event.target[i].checked};

        }

        this.setState({options: !this.state.options, roles: options});
        console.log(this.state.roles);


    };

    /**
     * Shows the information panel.
     */
    info = () => {
        this.setState({info: !this.state.info})

    };

    /**
     * Shows the input panel
     */
    showInputs = () => {
        this.setState({input: !this.state.input});
    };

    /**
     * Shows the join game fields.
     */
    join = () => {
        this.setState({join: !this.state.join})
    };


     namesContainValidChars = (name) => {
         // string containing all invalid characters
         const invalid_chars = "/?#\\.";
         // filter out all names containing invalid characters
         for(let i in invalid_chars) {
             let c = invalid_chars[i];
             if(name.includes(c)) {
                 return false;
             }
         }
         return true;
     };

     isDuplicateName = (name) => {
         // use trimmed name to avoid stuff like 'A     ' and '     A' being considered different names
         name = name.trim();
         for(let i in this.state.players) {
             if(this.state.players[i].name.trim() === name) {
                 return true;
             }
         }
         return false;
     };


    /**
     * Adds a player to the game
     * @param event sumbit event for the player form.
     */
    playerSubmit = (event) => {
        event.preventDefault();
        const name = event.target[0].value;
        document.getElementById("player-name-input").reset();
        this.setState({error: ""});
        if (name === "") {
            return;
        }

        if (!this.namesContainValidChars(name)) {
            this.setState({error: "Name contains invalid characters."});
            return;
        }

        if(this.isDuplicateName(name)) {
            this.setState({error: "Duplicate names are not allowed."});

            return;
        }

        if (name.length > 30) {
            this.setState({error: "Names must not be longer than 30 characters"});

            return;
        }
        const players = this.state.players;
        this.setState({player_key: this.state.player_key + 1} );
        const key = this.state.player_key;

        players.push({key: this.state.player_key, name: name, value:  <PlayerTag key={this.state.player_key}
                                                                     change={() => this.removePlayer(key)}
                                                                     name={name}/>});
        this.setState({players: players});

    };

    /**
     * Removes a player from the game.
     * @param key leading to player.
     */
    removePlayer = (key) => {
        const players = this.state.players;
        let i;
        for (i = 0; i < players.length; i++) {
            if (players[i].key === key) {
               break;
            }
        }

        players.splice(i, 1);
        this.setState({players: players});

    };

    /**
     * Redirects to a new game on submit of the Create Game form.
     */
     postToGame = () => {
        console.log("posting");
        fetch('/names', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',

            },
            body: JSON.stringify({
                names: this.names()
            })
        }).then(response => {
            return response.json();
        }).then(data => {
            console.log(data);
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


    /**
     * Redirects to a new custom game on submit of the Create Game form.
     */
     postToCustomGame = () => {
        console.log("posting");
        // construct custom info to post
        let customBody = {};
        for(let i in this.state.roles) {
            let elt = this.state.roles[i];
            customBody[elt.key] = elt.value;
        }

        let duplicates = customBody["Duplicate Roles"];
        // remove key from copy so that the only info in the json is role related
        delete customBody["Duplicate Roles"];
        // parse role keys that need to be changed
        if(customBody["Lone Percival"]) {
           delete customBody["Lone Percival"];
           customBody["LonePercival"] = true;
        }
        if(customBody["Lone Lovers"]) {
           delete customBody["Lone Lovers"];
           customBody["LoneTristan"] = true;
           customBody["LoneIseult"] = true;
        }
        if(customBody["Lovers"]) {
            delete customBody["Lovers"];
            customBody["Tristan"] = true;
            customBody["Iseult"] = true;
        }
        console.log("CUSTOM ROLE INFO:");
        console.log(customBody);

        fetch('/names', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',

            },
            body: JSON.stringify({
                names: this.names(),
                custom: customBody,
                duplicates: duplicates
            })
        }).then(response => {
            return response.json();

        }).then(data => {
            console.log(data);
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


    /**
     * Checks if a game can be started, and displays the appropriate button.
     * @returns {*}
     */
    isValid = () => {
        if ((this.state.players.length === 5
            || this.state.players.length === 7
            || this.state.players.length === 8
            || this.state.players.length === 10)) {

            return (
                <div>
                    <button onClick={this.postToGame} className={"large_button"}>
                        Start Game
                    </button>
                    <button onClick={this.postToCustomGame} className={"large_button"}>
                        Start Custom Game
                    </button>
                </div>
           );
        } else {
            return (
                <div>
                    <button className={"invalid_start"}>Start Game</button>
                    <button className={"invalid_start"}>Start Custom Game</button>
                </div>)

        }
    };

    /**
     * Forwards the player to the game specified by game id.
     * @returns {*}
     */
    forwardToGame = (event) => {
        event.preventDefault();
        const id = event.target[0].value;
        const url = "/isGame/" + id;
        fetch(url, {
            method: "GET"
        }).then(response => {
            return response.json();
        }).then (data => {
            if (data) {
                this.setState({join_redirect: <Redirect to={{ pathname: "/" + id}} />})

            } else {
                this.setState({join_error: "Game ID not found"});
            }
        });
    };

    /**
     * Retrieves a list of String player names from the list of IDs.
     * @returns {Array}
     */
    names = () => {
        const array = [];
        const names = this.state.players.values();
        let next = names.next();
        while (!next.done) {
            array.push(next.value.name);
            next = names.next();
        }
        return array;
    };




    /**
     * Renders the app.
     * @returns {*}
     */
  render() {
      let array = [];
      const names = this.state.players.values();
      let next = names.next();
      while (!next.done) {
          array.push(next.value.name);
          next = names.next();
      }
      return (
      <div className="lobby">
          {this.state.redirect}
          {this.state.join_redirect}
                  <Options options={this.state.roles} display={this.state.options} submit={this.optionsSubmit} />
\        <h1>
          THavalon
        </h1>
            <button className={"large_button"} onClick={this.showInputs}>
              Create Game
          </button>

          {this.state.input ?
              <div>
              <form className="player_input" id={"player-name-input"} onSubmit={this.playerSubmit}>
                  <input type="text" id ={"input-field"} placeholder={"Enter player name"}/>
                  <br></br>
                  <div className={"error"}>{this.state.error}</div>
                  <input type={"submit"} className={"player-submit"} id={"add-submit"} value={"Add"}/>
                  <button className={"small_button"} onClick={this.options}>Options</button>

              </form>

                  <div className={"player_tags"}>
                      {this.state.players.map(function(element) {
                          return element.value;
                          })}
                  </div>
                  {this.isValid()}
          </div> : null }
          <button className={"large_button"} onClick={this.join}>
              Join Game
          </button>
          {this.state.join ?
              <div>
                  <form className="player_input" onSubmit={this.forwardToGame}>
                      {this.state.join_input}
                          <input type={"submit"} className={"player-submit"} id={"join-submit"} value={"Join"}/>
                      <br></br>
                      <div className="error"> {this.state.join_error} </div>
                  </form>
                  <RecentGames/>

              </div> : null }

              <div className="info">
                  {this.state.info ?
                      <div className={"rules_wrapper"}>
                          <button className={"exit_button"} onClick={this.info}> x </button>
                          <div className={"rules"}>
                          <h1>Rules</h1>
                              <Rules />

                      </div>
                      </div>: null}
                  <button className={"info_button"} onClick={this.info}>?</button>
              </div>
      </div>

    );
  }
}

export default App;
