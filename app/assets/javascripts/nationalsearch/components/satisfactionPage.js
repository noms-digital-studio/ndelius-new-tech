import {Component} from 'react'
import GovUkPhaseBanner from './govukPhaseBanner';
import PropTypes from 'prop-types'
import moment from 'moment'

class SatisfactionPage extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        fetch(this.props)
    }

    render() {
        return (
            <div>
                <GovUkPhaseBanner basicVersion={true}/>
                <h1 className="heading-xlarge no-margin-bottom">National Search Satisfaction</h1>
                <div className="grid-row margin-top">
                    <div>

                        <canvas style={{backgroundColor: '#cccccc'}} ref={(canvas) => { this.canvas = canvas; }}/>

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
        const key = yearNumber + '-' + weekNumber;
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