import React, { Component, Fragment } from 'react';
import * as PropTypes from 'prop-types';

class Accordion extends Component {

    constructor(props) {
        super(props);
        this.state = { isOpen: false };
    }

    render() {
        return (
            <Fragment>
                <div className="moj-accordion">
                    <h2 className="govuk-heading-m govuk-!-margin-0 moj-!-color-blue moj-util-clickable"
                        onClick={ () => {
                            this.setState({ isOpen: !this.state.isOpen });
                        } }>{ this.props.label }<span
                        className="qa-accordion-label govuk-heading-xl govuk-!-margin-bottom-0 moj-accordion__toggle"
                        style={ { lineHeight: 0.7 } }>{ this.state.isOpen ? (<Fragment>&ndash;</Fragment>) : '+' }</span>
                    </h2>
                    <div className={ 'qa-accordion-content govuk-!-margin-top-4' + (this.state.isOpen ? '' : ' govuk-visually-hidden') }>
                        <div className="moj-inside-panel">
                            { this.props.content }
                        </div>
                    </div>
                </div>
                <hr className="govuk-!-margin-0 govuk-!-margin-top-3 govuk-!-margin-bottom-3"/>
            </Fragment>
        );
    }
}

Accordion.propTypes = {
    label: PropTypes.string,
    content: PropTypes.node
};

export default Accordion;