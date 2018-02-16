import {Component} from 'react'
import UniqueVisitsCounts from '../containers/uniqueVisitsCountContainer';
import AllVisitsCounts from '../containers/allVisitsCountContainer';
import AllSearchesCounts from '../containers/allSearchesCountContainer';
import TimeRangeLink from '../containers/analyticsTimeRangeLinkContainer';
import GovUkPhaseBanner from './govukPhaseBanner';
import {LAST_HOUR, TODAY, THIS_WEEK, LAST_SEVEN_DAYS, LAST_THIRTY_DAYS, THIS_YEAR, ALL} from '../actions/analytics'
import PropTypes from 'prop-types'

class AnalyticsPage extends Component {
    constructor(props) {
        super(props);
    }
    componentWillMount() {
        fetch(this.props)
    }
    componentWillReceiveProps(nextProps) {
        fetch(nextProps)
    }

    render() {
        return (
            <div>
                <noscript>You need to enable JavaScript to run this app.</noscript>
                <GovUkPhaseBanner basicVersion={true}/>
                <h1 className="heading-xlarge no-margin-bottom">National Search Analytics</h1>
                <div className="grid-row margin-top">
                    <div className="column-two-thirds">
                        <UniqueVisitsCounts description='Unique visits'/>
                        <AllVisitsCounts description='All visits'/>
                        <AllSearchesCounts description='All searches'/>
                    </div>
                    <div className="column-one-third">
                        <NavigationPanel/>
                    </div>
                </div>
            </div>)
    }
}

const fetch = props => {
    const {fetchVisitCounts, currentTimeRange} = props;
    fetchVisitCounts(currentTimeRange)
}


const NavigationPanel = () => (
            <nav className="js-stick-at-top-when-scrolling">
                <div className="nav-header"/>
                <h3 className="heading-medium no-margin-top no-margin-bottom">Analytics for</h3>
                <TimeRangeLink timeRange={LAST_HOUR}>Last hour</TimeRangeLink><br/>
                <TimeRangeLink timeRange={TODAY}>Today</TimeRangeLink><br/>
                <TimeRangeLink timeRange={THIS_WEEK}>This week</TimeRangeLink><br/>
                <TimeRangeLink timeRange={LAST_SEVEN_DAYS}>Last 7 days</TimeRangeLink><br/>
                <TimeRangeLink timeRange={LAST_THIRTY_DAYS}>Last 30 days</TimeRangeLink><br/>
                <TimeRangeLink timeRange={THIS_YEAR}>This year</TimeRangeLink><br/>
                <TimeRangeLink timeRange={ALL}>All time</TimeRangeLink><br/>
            </nav>
        )

AnalyticsPage.propTypes = {
    fetchVisitCounts: PropTypes.func.isRequired,
    currentTimeRange: PropTypes.string.isRequired
}


export default AnalyticsPage;