import { connect } from 'react-redux'
import { fetchSatisfactionCounts } from '../actions/analytics'
import satisfactionPage from '../components/satisfactionPage'

export default connect(
    state => ({
        currentTimeRange: state.analytics.timeRange,
        satisfactionCounts: state.analytics.satisfactionCounts
    }),
    dispatch => ({
        fetchSatisfactionCounts: () => dispatch(fetchSatisfactionCounts())
    }),
)(satisfactionPage)