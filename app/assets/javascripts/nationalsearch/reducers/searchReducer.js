import {
    REQUEST_SEARCH,
    SEARCH_RESULTS,
    CLEAR_RESULTS
} from '../actions/search'

const searchResults = (state = {searchTerm: '', results: []}, action) => {
    switch (action.type) {
        case REQUEST_SEARCH:
            return {searchTerm: action.searchTerm, results: state.results};
        case SEARCH_RESULTS:
            if (state.searchTerm === action.searchTerm) {
                return {searchTerm: state.searchTerm, results: mapResults(action.results.offenders, state.searchTerm)};
            }
            return state
        case CLEAR_RESULTS:
            return {searchTerm: '', results: []};
        default:
            return state
    }
};

export default searchResults

function mapResults(results = [], searchTerm) {
    return results.map(offenderDetails => {
        return {
            offenderId: offenderDetails.offenderId,
            firstName: offenderDetails.firstName,
            surname: offenderDetails.surname,
            crn: offenderDetails.crn,
            dateOfBirth: offenderDetails.dateOfBirth,
            risk: offenderDetails.risk,
            currentOffender: offenderDetails.currentOffender,
            gender: offenderDetails.gender,
            age: offenderDetails.age,
            aliases: offenderDetails.aliases.map((alias) => {
                return {
                    firstName: alias.firstName,
                    surname: alias.surname
                }
            }).filter(item => anyMatch(item, searchTerm)),
            previousSurname: whenMatched(offenderDetails.previousSurname, searchTerm),
            addresses: offenderDetails.addresses.map((address) => {
                return {
                    addressNumber: address.addressNumber,
                    buildingName: address.buildingName,
                    streetName: address.streetName,
                    town: address.town,
                    county: address.county,
                    postcode: address.postcode
                }
            }).filter(item => anyMatch(item, searchTerm))
        }
    });
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
    return searchTerm.split(' ')
        .filter(searchWord => searchWord) // remove empty terms
        .map(searchWord => RegExp(searchWord, "i").test(text))
        .reduce((accumulator, currentValue) => accumulator || currentValue, false)
}