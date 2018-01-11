import { connect } from 'react-redux'
import { addNewOffender } from '../actions/navigate'
import addNewOffenderLink from '../components/addNewOffenderLink.jsx'

const mapStateToProps = () => {
    return {}
};

const mapDispatchToProps = (dispatch) => {
    return {
        addNewOffender: () => {
            dispatch(addNewOffender())
        }
    }
};

const addNewOffenderLinkContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(addNewOffenderLink);

export default addNewOffenderLinkContainer