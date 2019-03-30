import React, { Component } from 'react';
import './css/App.css';
import './css/Options.css';


/**
 * Models the options window, which toggles the role options for the name. Requires three props:
 * a submit function, an options array, and a onChange function which will be mapped to each
 * checkbox.
 */
class Options extends Component {

    constructor(props) {
        super(props);
        let count = 0;
        this.state = ({
        form: <form className={"option_form"} onSubmit={(event) => this.props.submit(event)}>
            <div className={"checks"}>
                {this.props.options.map(function (element) {
                    count++;
                    return (<div key={count} className={"option_ele"}>
                        <label className="label">
                            <input className={"check slider"} type={"checkbox"} name={element.key} defaultChecked={element.value}/>
                            {element.key}
                        </label>
                    </div>);
                })}
            </div>
            <input className={"options_submit"} type={"submit"} value={"Done"}/>
        </form>
        })
    }


    onSubmit = (event) => {
        event.preventDefault();
        this.props.submit(event);
    };
    render() {
        let count = 0;
        console.log(this.props.options);
        return (
            <div className={"options_wrapper"}>
                {this.props.display ?
                    <div className={"options"}>
                        form: <form className={"option_form"} onSubmit={(event) => this.props.submit(event)}>
                        <div className={"checks"}>
                            {this.props.options.map(function (element) {
                                count++;
                                console.log(element);
                                return (<div key={count} className={"option_ele"}>
                                    <label className="label">
                                        <input className={"check slider"} type={"checkbox"} name={element.key} defaultChecked={element.value}/>
                                        {element.key}
                                    </label>
                                </div>);
                            })}
                        </div>
                        <input className={"options_submit"} type={"submit"} value={"Done"}/>
                    </form>  </div>
                    : null
                }

            </div>
        );
    }
}

export default Options;