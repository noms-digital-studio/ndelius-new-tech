import {CLEAR_RESULTS, REQUEST_SEARCH, SEARCH_RESULTS} from '../actions/search'
import {flatMap} from '../../helpers/streams'

const searchResults = (state = {searchTerm: '', resultsReceived: false, results: [], suggestions: [], total: 0, pageNumber: 1}, action) => {
    switch (action.type) {
        case REQUEST_SEARCH:
            return {
                searchTerm: action.searchTerm,
                results: state.results,
                total: state.total,
                pageNumber: state.pageNumber,
                suggestions: state.suggestions,
                resultsReceived: state.resultsReceived
            };
        case SEARCH_RESULTS:
            if (state.searchTerm.indexOf(action.searchTerm) > -1) {
                return {
                    searchTerm: state.searchTerm,
                    pageNumber: action.pageNumber,
                    total: action.results.total,
                    results: mapResults(action.results.offenders, state.searchTerm),
                    suggestions: mapSuggestions(action.results.suggestions),
                    resultsReceived: true
                };
            }
            return state
        case CLEAR_RESULTS:
            return {
                searchTerm: '',
                results: [],
                suggestions: [],
                total: 0,
                pageNumber: 1,
                resultsReceived: false
            };
        default:
            return state
    }
};

export default searchResults

function mapResults(results = [], searchTerm) {
    return results.map(offenderDetails => {
        return {
            ...offenderDetails,
            aliases: offenderDetails.offenderAliases.map((alias) => {
                return {
                    ...alias
                }
            }).filter(item => anyMatch(item, searchTerm)),
            previousSurname: whenMatched(offenderDetails.previousSurname, searchTerm),
            addresses: offenderDetails.contactDetails.addresses.map((address) => {
                return {
                    ...address
                }
            }).filter(item => anyMatch(item, searchTerm))
        }
    });
}

const mapSuggestions = (suggestions) => {
    if (suggestions && suggestions.suggest && Object.getOwnPropertyNames(suggestions.suggest).length > 0) {
        return flatMap(Object.getOwnPropertyNames(suggestions.suggest), suggestField => suggestions.suggest[suggestField])
            .filter(searchedWordsWithSuggestions => searchedWordsWithSuggestions.options.length > 0)
    }

    return []
}

function anyMatch(item, searchTerm) {
    return Object.getOwnPropertyNames(item)
        .map(property => item[property])
        .map(text => matches(text, searchTerm))
        .reduce((accumulator, currentValue) => accumulator || currentValue, false)
}

function whenMatched(text, searchTerm) {
    return matches(text, searchTerm) ? text : null;
}

function matches(text, searchTerm) {
    return text && searchTerm.split(' ')
        .filter(searchWord => searchWord) // remove empty terms
        .map(searchWord => RegExp(searchWord, "i").test(text))
        .reduce((accumulator, currentValue) => accumulator || currentValue, false)
}