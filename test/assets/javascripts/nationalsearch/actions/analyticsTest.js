import {fetchVisitCounts} from './analytics'
import {expect} from 'chai';
import {stub} from 'sinon';


describe('fetchVisitCounts action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        global.$ = {
            getJSON: stub()
        }
    })

    describe('on fetch counts', () => {
        beforeEach(() => {
            global.$.getJSON.yields({uniqueUserVisits: 10, allVisits: 100})
            fetchVisitCounts()(dispatch)
        })
        it ('dispatches FETCHING_VISIT_COUNTS', () => {
            expect(dispatch).to.be.calledWith({type: 'FETCHING_VISIT_COUNTS'})
        })
        it ('dispatches VISIT_COUNTS with count data', () => {
            expect(dispatch).to.be.calledWith({type: 'VISIT_COUNTS', uniqueUserVisits: 10, allVisits: 100})
        })
    })

})

