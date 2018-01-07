const searchResults = (state = {searchTerm: '', results: []}, action) => {
    switch (action.type) {
        case 'REQUEST_SEARCH':
            return {searchTerm: action.searchTerm, results: state.results};
        case 'SEARCH_RESULTS':
            return {searchTerm: state.searchTerm, results: action.results};
        default:
            return state
    }
};

export default searchResults