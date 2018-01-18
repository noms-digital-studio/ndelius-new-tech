import { connect } from 'react-redux'
import { addContact } from '../actions/navigate'
import addContactLink from '../components/addContactLink'

const mapStateToProps = (state) => {
    return {}
};

const mapDispatchToProps = (dispatch) => {
    return {
        addContact: (offenderId) => {
            dispatch(addContact(offenderId))
        }
    }
};

const addContactLinkContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(addContactLink);

export default addContactLinkContainer