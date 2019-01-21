import React, { Component, Fragment } from 'react'
import * as PropTypes from 'prop-types'

class Accordion extends Component {

  constructor (props) {
    super(props)
    this.state = {isOpen: false}
  }

  render () {

    let id = this.props.id

    return (
      <Fragment>
        <div className="govuk-accordion__section">
          <div className="govuk-accordion__section-header">
            <h2 className="govuk-accordion__section-heading">
              <span className="govuk-accordion__section-button" id={ `accordion-default-heading-${id}` }>
                { this.props.label }
              </span>
            </h2>
          </div>
          <div id={`accordion-default-content-${id}`} className="govuk-accordion__section-content"
               aria-labelledby={ `accordion-default-heading-${id}` }>
            { this.props.children }
          </div>
        </div>
      </Fragment>
    )
  }
}

Accordion.propTypes = {
  label: PropTypes.string,
  children: PropTypes.node
}

export default Accordion