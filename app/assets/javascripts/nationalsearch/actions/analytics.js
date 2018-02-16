export const VISIT_COUNTS = 'VISIT_COUNTS'
export const FETCHING_VISIT_COUNTS = 'FETCHING_VISIT_COUNTS'
export const TIME_RANGE = 'TIME_RANGE'

export const LAST_HOUR = 'LAST_HOUR';
export const TODAY = 'TODAY';
export const THIS_WEEK = 'THIS_WEEK';
export const LAST_SEVEN_DAYS = 'LAST_SEVEN_DAYS';
export const LAST_THIRTY_DAYS = 'LAST_THIRTY_DAYS';
export const THIS_YEAR = 'THIS_YEAR';
export const ALL = 'ALL';

export const visitCounts = data => ({type: VISIT_COUNTS, ...data})
export const changeTimeRange = timeRange => ({type: TIME_RANGE, timeRange})
export const fetchingVisitCounts = () => ({type: FETCHING_VISIT_COUNTS})

const fetchVisitCounts = (duration) => (
    dispatch => {
        dispatch(fetchingVisitCounts())
        $.getJSON(`/nationalSearch/analytics/visitCounts?duration=${duration}`, data => {
            dispatch(visitCounts(data))
        });

    }
)

export {fetchVisitCounts}