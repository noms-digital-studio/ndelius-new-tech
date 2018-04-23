import { connect } from 'react-redux'
import areaFilter from '../components/areaFilter'
import {addAreaFilter, removeAreaFilter, search} from '../actions/search'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm,
        byProbationArea: state.search.byProbationArea,
        areaFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter)
    }),
    dispatch => ({
        addAreaFilter: (probationAreaCode, probationAreaDescription)  => dispatch(addAreaFilter(probationAreaCode, probationAreaDescription)),
        removeAreaFilter: probationAreaCode => dispatch(removeAreaFilter(probationAreaCode)),
        search: (searchTerm, probationAreasFilter) => dispatch(search(searchTerm, probationAreasFilter))
    })
)(areaFilter)