import React from "react";
import './styles/Game.scss'
import { url } from './App'
/**
 * Models the Game info.
 */
class Player extends React.Component {


    constructor(props) {
        super(props);
        this.state = {
            parsed: []
        }
    }
    componentDidMount() {
        this.constructInfo(this.props.location.state.info.information);
    }

    /**
     * Parses a JSON info object
     * @param info
     */
    constructInfo = (info) => {
        console.log(info);
        info = JSON.parse(info);
        let infoArray = info.alerts;
        infoArray = infoArray.concat(info.rolePresent, info.perfect, info.seen, info.pairSeen);
        infoArray = infoArray.map(e => e.replace('You see', ''));

        this.setState({parsed: infoArray})
    };
    /**
     * Requests the player data for the game that is happening.
     */
    render() {
        console.log(this.props.location.state);
        const info = this.props.location.state.info;
        const isGood = info.allegiance === 'Good';
        const color = isGood ? 'green' : 'red';

        let des = info.description;
        des = des.replace(/You are on the good team/, '');
        des = des.replace(/You are a member of the Evil council/, '');

        des = des.split('\n');
        des = des.map(ele => {
            console.log(typeof ele);
            if (ele.includes('Ability')) {
                return <span className={'bold'}> {ele} </span>;
            } else {
                return  <p>{ele}</p>;
            }
        });
        console.log(this.state.parsed);

        des = <div className={'des'}> {des} </div>;

        return (
            <div className={'Player'}>
                <h1>{info.name}
                </h1>
                <div className={'block role'}>
                    You are <span className={color + ' role-text'}> {info.role} </span>
                </div>
                <div className={'block info'}>
                    You are on the <span className={color + ' role-text'}> {info.allegiance} </span>Team
                    <div className={'description'}>
                    {des}
                    </div>
                    <div className={'seen'}>
                        {this.state.parsed.length !== 0 ? <div> You see:
                        <ul>
                        {this.state.parsed.map(ele => <li> {ele} </li>)}
                        </ul>
                            </div> : null}

                    </div>
                </div>
            </div>
        )
    }
}

export default Player;