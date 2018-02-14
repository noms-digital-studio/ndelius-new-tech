import { connect } from 'react-redux'
import { fetchVisitCounts } from '../actions/analytics'
import analyticsPage from '../components/analyticsPage'

export default connect(
    () => ({}),
    dispatch => ({
        fetchVisitCounts: () => dispatch(fetchVisitCounts())
    }),
)(analyticsPage)