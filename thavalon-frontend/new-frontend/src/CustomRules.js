import React, { Component } from 'react';
import './styles/App.scss';


/**
 * Models the options window, which toggles the role options for the name. Requires three props:
 * a submit function, an options array, and a onChange function which will be mapped to each
 * checkbox.
 */
class CustomRules extends Component {
    render() {
        let count = 0;
        return (
            <div className={"CustomRules"}>
                {this.props.display ?
                    <form className={"rules-form"} onSubmit={(event) => this.props.submit(event)}>
                        <div className={"checks"}>
                            {this.props.options.map(function (element) {
                                count++;
                                return (<div key={count} className={"rules-ele"}>
                                    <label className="label">
                                        <input className={"check"} type={"checkbox"} name={element.key} defaultChecked={element.value}/>
                                        {element.key}
                                    </label>
                                </div>);
                            })}
                        </div>
                        <input className={"rules-submit"} type={"submit"} value={"Done"}/>
                    </form>
                    : null
                }

            </div>
        );
    }
}

export default CustomRules;