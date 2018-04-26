import { connect } from 'react-redux'
import areaFilter from '../components/filter'
import {addAreaFilter, removeAreaFilter, search} from '../actions/search'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm,
        filterValues: removeMyProbationAreas(state.search.byProbationArea, state.search.myProbationAreas),
        currentFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter),
        name: 'all-providers',
        title: 'Other providers'
    }),
    dispatch => ({
        addToFilter: (probationAreaCode, probationAreaDescription)  => dispatch(addAreaFilter(probationAreaCode, probationAreaDescription)),
        removeFromFilter: probationAreaCode => dispatch(removeAreaFilter(probationAreaCode)),
        search: (searchTerm, probationAreasFilter) => dispatch(search(searchTerm, probationAreasFilter))
    })
)(areaFilter)

export const removeMyProbationAreas = (byProbationArea, myProbationAreas) => {
    return byProbationArea.reduce((updatedByProbationArea, area) => {
        if (!myProbationAreas[area.code]) {
            updatedByProbationArea.push(area)
        }
        return updatedByProbationArea
    }, [])
}

