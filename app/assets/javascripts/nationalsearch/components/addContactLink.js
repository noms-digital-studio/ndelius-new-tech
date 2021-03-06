import React from 'react'
import PropTypes from 'prop-types'

const AddContactLink = ({ offenderId, rankIndex, surname, firstName, addContact, highlight, tabIndex }) => (
  <span>
    <a tabIndex={tabIndex} href='javascript:' className='govuk-link govuk-link--no-visited-state'
       title={surname ? `Add contact to ${surname}, ${firstName}` : 'Add contact'}
       onClick={() => addContact(offenderId, rankIndex, highlight)}>Add contact</a>
  </span>
)

AddContactLink.propTypes = {
  offenderId: PropTypes.number.isRequired,
  rankIndex: PropTypes.number.isRequired,
  addContact: PropTypes.func.isRequired,
  tabIndex: PropTypes.string,
  surname: PropTypes.string,
  firstName: PropTypes.string,
  highlight: PropTypes.object
}

export default AddContactLink
