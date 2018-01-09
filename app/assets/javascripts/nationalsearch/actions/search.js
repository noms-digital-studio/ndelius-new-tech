function requestSearch(searchTerm){
    return {
        type: 'REQUEST_SEARCH',
        searchTerm
    }
}

function searchResults(results){
    return {
        type: 'SEARCH_RESULTS',
        results
    }
}

const performSearch = _.debounce((dispatch, searchTerm) => {
    $.getJSON('searchOffender/' + searchTerm, data => {
        dispatch(searchResults(data))
    });
}, 500);



export function search(dispatch, searchTerm) {

    dispatch(requestSearch(searchTerm));

    performSearch(dispatch, searchTerm);
}