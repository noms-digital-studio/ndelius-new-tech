export const REQUEST_SEARCH = 'REQUEST_SEARCH'
export const SEARCH_RESULTS = 'SEARCH_RESULTS'
export const CLEAR_RESULTS = 'CLEAR_RESULTS'

const requestSearch = (searchTerm) => ({
        type: REQUEST_SEARCH,
        searchTerm
    })

const searchResults = (searchTerm, results) => ({
        type: SEARCH_RESULTS,
        searchTerm,
        results
    })

const clearResults = () => ({type: CLEAR_RESULTS})

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