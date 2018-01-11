import { connect } from 'react-redux'
import {showOffenderDetails} from '../actions/navigate'
import offenderSummaryTitle from '../components/offenderSummaryTitle.jsx'

const mapStateToProps = () => {
    return {}
};

const mapDispatchToProps = dispatch => {
    return {
        showOffenderDetails: (offenderId) => {
            dispatch(showOffenderDetails(offenderId))
        }
    }
}

const offenderSummaryTitleContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(offenderSummaryTitle);

export default offenderSummaryTitleContainer