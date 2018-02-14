import {
    VISIT_COUNTS,
    FETCHING_VISIT_COUNTS
} from '../actions/analytics'

const analytics = (state = {uniqueUserVisits: 0, allVisits: 0, fetching: false}, action) => {
    switch (action.type) {
        case VISIT_COUNTS:
            return {
                ...state,
                uniqueUserVisits: action.uniqueUserVisits,
                allVisits: action.allVisits,
                fetching: false
            };
        case FETCHING_VISIT_COUNTS:
            return {
                ...state,
                fetching: true
            };
        default:
            return state
    }
};

export default analytics

