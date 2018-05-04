import {connect} from 'react-redux'
import analyticsPieChart from '../components/analyticsPieChart'

export default connect(
    state => ({
        numberToCountData: state.analytics.filterCounts,
        fetching: state.analytics.fetching,
        labelMapper
    }),
    () => ({})
)(analyticsPieChart)

export const labelMapper = data => Object.getOwnPropertyNames(data).map(eventType => eventTypeMap[eventType])

export const onlySearchEvents = eventOutcome => Object.getOwnPropertyNames(eventOutcome).reduce((outcomes, name) => {
    if (Object.getOwnPropertyNames(eventTypeMap).indexOf(name) !== -1) {
        outcomes[name] = eventOutcome[name]
    }
    return outcomes
}, {})

const eventTypeMap = {
    'hasUsedMyProvidersFilterCount': 'My providers',
    'hasUsedOtherProvidersFilterCount': 'Other providers',
    'hasUsedBothProvidersFilterCount': 'Both sets',
    'hasNotUsedFilterCount': 'No filter'
}