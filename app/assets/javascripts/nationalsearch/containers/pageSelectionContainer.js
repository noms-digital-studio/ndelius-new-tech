import { connect } from 'react-redux'
import { search } from '../actions/search'
import pageSelection from '../components/pageSelection.jsx'

const mapStateToProps = (state) => {
    return {}
};

const mapDispatchToProps = (dispatch) => {
    return {
        search: (searchTerm, pageNumber) => {
            search(searchTerm, pageNumber)
        }
    }
};

const pageSelectionContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(pageSelection);

export default pageSelectionContainer