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
                return {searchTerm: state.searchTerm, results: mapResults(action.results, state.searchTerm)};
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
            offenderId: offenderDetails['OFFENDER_ID'],
            firstName: offenderDetails['FIRST_NAME'],
            surname: offenderDetails['SURNAME'],
            crn: offenderDetails['CRN'],
            dateOfBirth: '19/07/1965',
            risk: offenderDetails['CURRENT_HIGHEST_RISK_COLOUR'],
            currentOffender: offenderDetails['CURRENT_DISPOSAL'] === 1,
            gender: offenderDetails['GENDER_ID'] === 545 ? 'Male' : 'Female',
            age: 2017 - offenderDetails['DATE_OF_BIRTH_DATE'].substring(0, 4),
            aliases: offenderDetails['ALIASES'].map((alias) => {
                return {
                    firstName: alias['FIRST_NAME'],
                    surname: alias['SURNAME']
                }
            }).filter(item => anyMatch(item, searchTerm)),
            previousSurname: whenMatched(offenderDetails['PREVIOUS_SURNAME'], searchTerm),
            addresses: offenderDetails['ADDRESSES'].map((address) => {
                return {
                    addressNumber: address['ADDRESS_NUMBER'].toString(),
                    buildingName: address['BUILDING_NAME'],
                    streetName: address['STREET_NAME'],
                    town: address['TOWN_CITY'],
                    county: address['COUNTY'],
                    postcode: address['POSTCODE']
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