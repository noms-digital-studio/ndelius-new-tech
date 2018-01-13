import {search} from './search'
import {expect} from 'chai';
import {stub} from 'sinon';


describe('search action', () => {
    let dispatch;

    beforeEach(() => {
        dispatch = stub()
        global.$ = {
            getJSON: stub()
        }
    })

    describe('on search', () => {
        context('when blank search term', () => {
            it ('sends a CLEAR_RESULTS', () => {
                search(dispatch, '')
                expect(dispatch).to.be.calledWith({type: 'CLEAR_RESULTS'})
            })
        })
        context('with a search term', () => {
            beforeEach(() => {
                global.$.getJSON.yields({offenders: []})
            })
            it ('dispatches REQUEST_SEARCH with searchTerm', () => {
                search(dispatch, 'Mr Bean')
                expect(dispatch).to.be.calledWith({type: 'REQUEST_SEARCH', searchTerm: 'Mr Bean'})
            })
            it ('dispatches SEARCH_RESULTS with searchTerm and results', () => {
                search(dispatch, 'Mr Bean')
                expect(dispatch).to.be.calledWith({type: 'SEARCH_RESULTS', results: {offenders: []}, searchTerm: 'Mr Bean'})
            })
        })
    })

})

