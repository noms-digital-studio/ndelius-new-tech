import { connect } from 'react-redux'
import searchTypeSelector from '../components/searchTypeSelector'
import {searchTypeChanged} from '../actions/search'

export default connect(
    state => ({
        searchType: state.search.searchType
    }),
    dispatch => ({
        searchTypeChanged: searchType => dispatch(searchTypeChanged(searchType)),
    })
)(searchTypeSelector)