import {
    ADD_CONTACT,
    LEGACY_SEARCH,
    SHOW_OFFENDER_DETAILS,
    ADD_NEW_OFFENDER
} from '../actions/navigate'

const navigate = (state = {shouldClose: true, nextPage: 'XXXXXX'}, action) => {
    switch (action.type) {
        case ADD_CONTACT:
            return {shouldClose: true, nextPage: 'addContact', parameters: [{offenderId: action.offenderId}]};
        case LEGACY_SEARCH:
            return {shouldClose: true, nextPage: 'legacySearch', parameters: []};
        case SHOW_OFFENDER_DETAILS:
            return {shouldClose: true, nextPage: 'showOffenderDetails', parameters: [{offenderId: action.offenderId}]};
        case ADD_NEW_OFFENDER:
            return {shouldClose: true, nextPage: 'addNewOffender', parameters: []};
        default:
            return state
    }
};

export default navigate

