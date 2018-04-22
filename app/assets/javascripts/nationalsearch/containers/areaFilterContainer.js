import { connect } from 'react-redux'
import areaFilter from '../components/areaFilter'
import {addAreaFilter, removeAreaFilter, search} from '../actions/search'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm,
        byProbationArea: state.search.byProbationArea,
        areaFilter: state.search.probationAreasFilter
    }),
    dispatch => ({
        addAreaFilter: probationAreaCode => dispatch(addAreaFilter(probationAreaCode)),
        removeAreaFilter: probationAreaCode => dispatch(removeAreaFilter(probationAreaCode)),
        search: (searchTerm, probationAreasFilter) => dispatch(search(searchTerm, probationAreasFilter))
    })
)(areaFilter)