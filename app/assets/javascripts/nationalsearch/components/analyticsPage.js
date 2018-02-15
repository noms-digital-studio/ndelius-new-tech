import {Component} from 'react'
import UniqueVisitsCounts from '../containers/uniqueVisitsCountContainer';
import AllVisitsCounts from '../containers/allVisitsCountContainer';
import GovUkPhaseBanner from './govukPhaseBanner';

class AnalyticsPage extends Component {
    constructor(props) {
        super(props);
    }
    componentWillMount() {
        const {fetchVisitCounts} = this.props;
        fetchVisitCounts()
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
                    </div>
                    <div className="column-one-third">
                        <NavigationPanel/>
                    </div>
                </div>
            </div>)
    }
}

const NavigationPanel = () => (
            <nav className="js-stick-at-top-when-scrolling">
                <div className="nav-header"/>
                <h3 className="heading-medium no-margin-top no-margin-bottom">Analytics for</h3>
                <a href="#" className="nav-item">Last hour</a><br/>
                <a href="#" className="nav-item">Today</a><br/>
                <a href="#" className="nav-item">This week</a><br/>
                <a href="#" className="nav-item">Last 7 days</a><br/>
                <a href="#" className="nav-item">Last 30 days</a><br/>
                <a href="#" className="nav-item">All year</a><br/>
                <a href="#" className="nav-item">All time</a>
            </nav>
        )

export default AnalyticsPage;