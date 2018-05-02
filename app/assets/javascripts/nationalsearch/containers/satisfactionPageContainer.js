import { connect } from 'react-redux'
import { fetchSatisfactionCounts } from '../actions/analytics'
import satisfactionPage from '../components/satisfactionPage'

export default connect(
    state => ({
        currentTimeRange: state.analytics.timeRange
    }),
    dispatch => ({
        fetchSatisfactionCounts: (timeRange) => dispatch(fetchSatisfactionCounts(timeRange))
    }),
)(satisfactionPage)