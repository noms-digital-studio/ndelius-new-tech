export const REQUEST_SEARCH = 'REQUEST_SEARCH'
export const SEARCH_RESULTS = 'SEARCH_RESULTS'
export const CLEAR_RESULTS = 'CLEAR_RESULTS'

function requestSearch(searchTerm){
    return {
        type: REQUEST_SEARCH,
        searchTerm
    }
}

function searchResults(searchTerm, results){
    return {
        type: SEARCH_RESULTS,
        searchTerm,
        results
    }
}

function clearResults(){
    return {
        type: CLEAR_RESULTS
    }
}

const performSearch = _.debounce((dispatch, searchTerm) => {
    $.getJSON('searchOffender/' + searchTerm, data => {
        dispatch(searchResults(searchTerm, data))
    });
}, 500);



export function search(dispatch, searchTerm) {
    if (searchTerm === '') {
        dispatch(clearResults())
    } else {
        dispatch(requestSearch(searchTerm));
        performSearch(dispatch, searchTerm);
    }
}