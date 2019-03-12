import React, { Component } from 'react';
import './css/App.css';
import './css/Options.css';


/**
 * Models the options window, which toggles the role options for the name. Requires three props:
 * a submit function, an options array, and a onChange function which will be mapped to each
 * checkbox.
 */
class Options extends Component {

    render() {

        return (<div className={"options_wrapper"}>
                <form className={"option_form"} onSubmit={this.props.submit}>
                    <div className={"checks"}>
                        {this.props.options.map(function (element) {
                        return (<div className={"option_ele"}>
                       <label className="label">
                           <input className={"check slider"} type={"checkbox"} id={element.key} key={element.key} defaultChecked={element.value}/>
                           <span className={"slider round"}> </span>
                           {element.key}
                       </label>

                        </div>);
                    })}
                    </div>

                    <input className={"options_submit"} type={"submit"} value={"Done"}/>
                </form>
            </div>

        );
    }
}

export default Options;