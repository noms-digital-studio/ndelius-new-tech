import {connect} from 'react-redux'
import offenderIdentity from '../components/offenderIdentity'

const mapStateToProps = state => ({
    offenderDetails: state.offenderSummary.offenderDetails,
    offenderConvictions: state.offenderSummary.offenderConvictions
})
export default connect(
    mapStateToProps,
    null
)(offenderIdentity)