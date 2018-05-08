import {Component} from 'react'
import GovUkPhaseBanner from './govukPhaseBanner';
import PropTypes from 'prop-types'
import moment from 'moment'
import {Link} from 'react-router'

class SatisfactionPage extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        fetch(this.props)
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
                        <canvas style={{backgroundColor: '#cccccc'}} ref={(canvas) => { this.canvas = canvas; }}/>
                    </div>
                    <div className="column-one-third">
                        <nav className="js-stick-at-top-when-scrolling">
                            <div className="nav-header"/>
                            <h3 className="heading-medium no-margin-top no-margin-bottom">Links for</h3>
                            <a href="/feedback/nationalSearch">National search feedback</a><br/>
                            <Link to ='/analytics' >National search analytics</Link>
                        </nav>
                        <input className="button margin-top" type="button" value="Refresh" onClick={() => this.onClickRefresh()}/>
                    </div>

                </div>
            </div>)
    }

    componentDidUpdate() {
        if (this.chart) {
            this.chart.destroy()
        }

        this.chart = new Chart(this.canvas.getContext('2d'), chartOptions(this.props.satisfactionCounts));
    }

}

const ratingData = function (satisfactionCounts, currentWeekNumber, yearNumber, ratingKey) {
    const vsCounts = satisfactionCounts[ratingKey];
    if (!vsCounts) return [];
    const vsCountsMap = {};
    vsCounts.forEach(data => {
        vsCountsMap[data.yearAndWeek] = data.count
    });
    const vsData = [];
    for (let weekNumber = 1; weekNumber <= currentWeekNumber; weekNumber++) {
        const key = yearNumber
                        + '-'
                        + (weekNumber - 1); // MongoDB $week numbers start at 0 unlike moment.js which starts at 1
        if (vsCountsMap[key]) {
            vsData.push(vsCountsMap[key])
        } else {
            vsData.push(0)
        }

    }
    return vsData;
};

const chartOptions = (satisfactionCounts) => {
    const yearNumber = '2018';
    const labels = [];
    const currentWeekNumber = moment().utc().week();
    for (let weekNumber = 1; weekNumber <= currentWeekNumber; weekNumber++) {
        labels.push(yearNumber + '-' + weekNumber)
    }

    return {
        type: 'line',
        data: {
            labels,
            datasets: [
                {
                    label: 'Very Satisfied',
                    data: ratingData(satisfactionCounts, currentWeekNumber, yearNumber, 'Very satisfied'),
                    backgroundColor: '#00ff00',
                    borderColor: '#00ff00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Satisfied',
                    data: ratingData(satisfactionCounts, currentWeekNumber, yearNumber, 'Satisfied'),
                    backgroundColor: '#FFFF00',
                    borderColor: '#FFFF00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Neither',
                    data: ratingData(satisfactionCounts, currentWeekNumber, yearNumber, 'Neither satisfied or dissatisfied'),
                    backgroundColor: '#FFCC00',
                    borderColor: '#FFCC00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Dissatisfied',
                    data: ratingData(satisfactionCounts, currentWeekNumber, yearNumber, 'Dissatisfied'),
                    backgroundColor: '#FF7F00',
                    borderColor: '#FF7F00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Very Dissatisfied',
                    data: ratingData(satisfactionCounts, currentWeekNumber, yearNumber, 'Very dissatisfied'),
                    backgroundColor: '#FF0000',
                    borderColor: '#FF0000',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },

            ]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        reverse: false
                    }
                }]
            }

        }
    }
}


const fetch = props => {
    const {fetchSatisfactionCounts} = props;
    fetchSatisfactionCounts()
}

SatisfactionPage.propTypes = {
    fetchSatisfactionCounts: PropTypes.func.isRequired,
    currentTimeRange: PropTypes.string.isRequired
}

export default SatisfactionPage;