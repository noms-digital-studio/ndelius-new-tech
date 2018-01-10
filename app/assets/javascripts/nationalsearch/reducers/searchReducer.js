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
                return {searchTerm: state.searchTerm, results: mapResults(action.results)};
            }
            return state
        case CLEAR_RESULTS:
            return {searchTerm: '', results: []};
        default:
            return state
    }
};

export default searchResults

function mapResults(results = []) {
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
            }),
            previousSurname: offenderDetails['PREVIOUS_SURNAME'],
            addresses: offenderDetails['ADDRESSES'].map((address) => {
                return {
                    addressNumber: address['ADDRESS_NUMBER'].toString(),
                    buildingName: address['BUILDING_NAME'],
                    streetName: address['STREET_NAME'],
                    town: address['TOWN_CITY'],
                    county: address['COUNTY'],
                    postcode: address['POSTCODE']
                }
            })
        }
    });
}