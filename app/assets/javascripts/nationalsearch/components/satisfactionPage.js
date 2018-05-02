import {Component} from 'react'
import AllVisitsCounts from '../containers/allVisitsCountContainer';
import TimeRangeLink from '../containers/analyticsTimeRangeLinkContainer';
import GovUkPhaseBanner from './govukPhaseBanner';
import {ALL, LAST_HOUR, LAST_SEVEN_DAYS, LAST_THIRTY_DAYS, THIS_WEEK, THIS_YEAR, TODAY} from '../actions/analytics'
import PropTypes from 'prop-types'

class SatisfactionPage extends Component {
    constructor(props) {
        super(props);
    }
    componentWillMount() {
        fetch(this.props)
        const interval = setInterval(() => fetch(this.props), 30000)
        if (interval.unref) {interval.unref()} // when running in mocha/node unref so test doesn't hang
    }
    componentWillReceiveProps(nextProps) {
        fetch(nextProps)
    }
    onClickRefresh() {
        fetch(this.props)
    }

    render() {
        return (
            <div>
                <GovUkPhaseBanner basicVersion={true}/>
                <h1 className="heading-xlarge no-margin-bottom">National Search Satisfaction</h1>
                <div className="grid-row margin-top">
                    <div className="column-two-thirds">

                        <AllVisitsCounts description='All visits'/>
                    </div>
                    <div className="column-one-third">
                        <NavigationPanel/>
                        <input className="button margin-top" type="button" value="Refresh" onClick={() => this.onClickRefresh()}/>
                    </div>
                </div>
            </div>)
    }
}

const fetch = props => {
    const {fetchSatisfactionCounts, currentTimeRange} = props;
    fetchSatisfactionCounts(currentTimeRange)
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

SatisfactionPage.propTypes = {
    fetchSatisfactionCounts: PropTypes.func.isRequired,
    currentTimeRange: PropTypes.string.isRequired
}


export default SatisfactionPage;