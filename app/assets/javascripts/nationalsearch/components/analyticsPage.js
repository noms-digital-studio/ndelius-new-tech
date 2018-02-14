import {Component} from 'react'
import UniqueVisitsCounts from '../containers/uniqueVisitsCountContainer';
import AllVisitsCounts from '../containers/allVisitsCountContainer';

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
                <div id="root">
                    <main id="content">
                        <div>
                            <h1>National Search Analytics</h1>
                        </div>
                        <div>
                            <UniqueVisitsCounts description='Unique visits'/>
                            <AllVisitsCounts description='All visits'/>
                        </div>
                    </main>
                </div>
            </div>)
    }
}


export default AnalyticsPage;