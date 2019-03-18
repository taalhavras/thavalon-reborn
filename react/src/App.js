import React, { Component } from 'react';
import './css/App.css';
import {Redirect} from 'react-router-dom';
import "./css/styles.css";
import "./PlayerTag";
import "./css/PlayerTag.css";
import PlayerTag from "./PlayerTag";
import Options from "./Options";

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
            {key: "Classic Arthur", value: false},
            {key: "Lancelot", value: true},
            {key: "Percival", value: true},
            {key: "Guinevere", value: true},
            {key: "Merlin", value: true},
            {key: "Titania", value: true},
            {key: "Nimue", value: false},
            {key: "Lovers", value: true},
            {key: "Lone Lovers", value: false},
            {key: "Mordred", value: true},
            {key: "Morgana", value: true},
            {key: "Maleagant", value: true},
            {key: "Oberon", value: true},
            {key: "Agravaine", value: true},
            {key: "Colgravance", value: true},


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
        };

    }

    /**
     * Shows the options panel.
     */
    options = () => {
        this.setState({options: !this.state.options})

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
    /**
     * Adds a player to the game
     * @param event sumbit event for the player form.
     */
    playerSubmit = (event) => {
        event.preventDefault();
        if (event.target[0].value === "") {
            return;
        }
        const players = this.state.players;
        this.setState({player_key: this.state.player_key + 1} );
        const key = this.state.player_key;

        players.push({key: this.state.player_key, name: event.target[0].value, value:  <PlayerTag key={this.state.player_key}
                                                                     change={() => this.removePlayer(key)}
                                                                     name={event.target[0].value}/>});
        this.setState({players: players});
        document.getElementById("player-name-input").reset();

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
            const redirect =
               <Redirect to={{ pathname: "/game/" + data["id"]}} />;
                       this.setState({redirect: redirect});
        }).catch(error => {
                console.log(error);
            });
    };

    /**
     * Checks if a game can be started, and displays the appropriate button.
     * @returns {*}
     */
    isValid = () => {
        if (this.state.players.length === 5
            || this.state.players.length === 7
            || this.state.players.length === 8
            || this.state.players.length === 10) {

            return (
                <button onClick={this.postToGame} className={"large_button"}>
                    Start Game
                </button>
           );
        } else {
            return (<button className={"invalid_start"}>Start Game</button>)

        }
    };

    /**
     * Forwards the player to the game specified by game id.
     * @returns {*}
     */
    forwardToGame = (event) => {
        event.preventDefault();
        const id = event.target[0].value;
        this.setState({join_redirect: <Redirect to={{ pathname: "/game/" + id}} />})
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

    options_change = (key) => {
        const roles = this.state.roles;
        roles[key] = !roles[key];
        this.setState({roles: roles});
    };

    /**
     * Renders the app.
     * @returns {*}
     */
  render() {
      const array = [];
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
        <h1>
          THavalon
        </h1>
            <button className={"large_button"} onClick={this.showInputs}>
              Create Game
          </button>
          {this.state.input ?
              <div>
                  {this.state.options ?
                  <div className={"options"}><Options options={this.state.roles} submit={this.options} />
                  </div> : null}
              <form className="player_input" id={"player-name-input"} onSubmit={this.playerSubmit}>
                  <input type="text" id ={"input-field"} placeholder={"Enter player name"}/>
                  <br></br>
                  <input type={"submit"} className={"player-submit"}  value={"Add"}/>
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
                          <input type={"submit"} className={"player-submit"}  value={"Join"}/>
                  </form>

              </div> : null }

              <div className="info">
                  {this.state.info ?
                      <div className={"rules_wrapper"}>
                          <button className={"exit_button"} onClick={this.info}> x </button>
                          <div className={"rules"}>
                          <h1>Rules</h1>
                          <h2> Lorum Ipsum</h2>
                          Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                          sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                          Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea
                          commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum
                          dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in
                          culpa qui officia deserunt mollit anim id est laborum
                          <h2>Lorum Ipsum</h2>
                          Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque
                          laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi
                          architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas
                          sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione
                          voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet,
                          consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore
                          magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam
                          corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit
                          qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?
                          <h2>Lorum Ipsum</h2>
                          At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum
                          deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident,
                          similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga.
                          Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est
                          eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis
                          voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis
                          aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae.
                          Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur
                          aut perferendis doloribus asperiores repellat.
                          <button className={"small_button"} onClick={this.info}>Done</button>
                      </div>
                      </div>: null}
                  <button className={"info_button"} onClick={this.info}>?</button>

              </div>
      </div>

    );
  }
}

export default App;
