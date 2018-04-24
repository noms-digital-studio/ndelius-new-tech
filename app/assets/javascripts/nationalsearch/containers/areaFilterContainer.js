import { connect } from 'react-redux'
import areaFilter from '../components/areaFilter'
import {addAreaFilter, removeAreaFilter, search} from '../actions/search'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm,
        byProbationArea: combineWithProbationAreaFilter(state.search.byProbationArea, state.search.probationAreasFilter),
        probationAreasFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter)
    }),
    dispatch => ({
        addAreaFilter: (probationAreaCode, probationAreaDescription)  => dispatch(addAreaFilter(probationAreaCode, probationAreaDescription)),
        removeAreaFilter: probationAreaCode => dispatch(removeAreaFilter(probationAreaCode)),
        search: (searchTerm, probationAreasFilter) => dispatch(search(searchTerm, probationAreasFilter))
    })
)(areaFilter)

export const combineWithProbationAreaFilter = (byProbationArea, probationAreasFilter) => {
    return Object.getOwnPropertyNames(probationAreasFilter).reduce((updatedByProbationArea, codeFromFilter) => {
        if (updatedByProbationArea.filter(area => area.code === codeFromFilter).length === 1) {
            return updatedByProbationArea
        }

        const copyOf = [].concat(updatedByProbationArea)
        copyOf.push({code: codeFromFilter, description: probationAreasFilter[codeFromFilter], count: 0})
        return copyOf
    }, byProbationArea)
}