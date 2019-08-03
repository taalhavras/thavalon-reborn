import React from "react";
import './styles/Game.scss'
import { url } from './App'
/**
 * Models the Game info.
 */
class DoNotOpen extends React.Component {


    constructor(props) {
        super(props);
        this.state = {
            info: []
        }
    }
    componentDidMount() {
        let total = this.props.location.state.info.map(player => {
            let info = player.information;
            info = JSON.parse(info);
            let infoArray = info.alerts;
            infoArray = infoArray.concat(info.rolePresent, info.perfect, info.seen, info.pairSeen);
            const isGood = player.allegiance === 'Good';
            const color = isGood ? 'green' : 'red';
            return {
                name: player.name,
                role: <div className={'bold ' + color}> {player.role} </div>,
                info: infoArray

            }
        });

        this.setState({info: total})


    }

    /**
     * Parses a JSON info object
     * @param info
     */
    /**
     * Requests the player data for the game that is happening.
     */
    render() {
        console.log(this.props.location.state.info);
        return (
            <div className={'DoNotOpen'}>
                <h1>Do Not Open</h1>
                <h2>Game {this.props.match.params.id} </h2>

                {this.state.info.map(ele => {
                    return (<div className={'block do-not-open-block'}>
                        <span className={'bold'}>{ele.name}</span> {ele.role}
                        {ele.info}
                    </div>);
                })}

            </div>
        )
    }
}

export default DoNotOpen;