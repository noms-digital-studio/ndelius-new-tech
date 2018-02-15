export const VISIT_COUNTS = 'VISIT_COUNTS'
export const FETCHING_VISIT_COUNTS = 'FETCHING_VISIT_COUNTS'

export const visitCounts = data => ({type: VISIT_COUNTS, ...data})
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