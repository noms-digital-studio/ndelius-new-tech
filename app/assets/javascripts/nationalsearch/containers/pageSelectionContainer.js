import { connect } from 'react-redux'
import { search, PAGE_SIZE } from '../actions/search'
import pageSelection from '../components/pageSelection'

const mapStateToProps = (state) => {
    return {
        searchTerm: state.search.searchTerm,
        pageSize: PAGE_SIZE,
        total: state.search.total,
        pageNumber: state.search.pageNumber
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        gotoPage: (searchTerm, pageNumber) => {
            search(dispatch, searchTerm, pageNumber)
        }
    }
};

const pageSelectionContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(pageSelection);

export default pageSelectionContainer