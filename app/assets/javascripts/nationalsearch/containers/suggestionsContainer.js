import { connect } from 'react-redux'
import suggestions from '../components/suggestions'
import {search} from '../actions/search'

const mapStateToProps = state => {
    return {
        suggestions: state.search.suggestions,
        searchTerm: state.search.searchTerm,
    }
}

const mapDispatchToProps = dispatch => {
    return {
        search: (searchTerm) => {
            search(dispatch, searchTerm)
        }
    }
}

const suggestionsContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(suggestions);

export default suggestionsContainer