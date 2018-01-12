import {ADD_CONTACT, SHOW_OFFENDER_DETAILS, LEGACY_SEARCH, ADD_NEW_OFFENDER} from '../actions/navigate'
import navigate  from './navigateReducer'
import {expect} from 'chai';

describe("navigateReducer", () => {
    describe("when in default state", () => {
        it('should set default state to not close', () => {
            const state = navigate(undefined, {type: '"@@redux/INIT"'})
            expect(state.shouldClose).to.equal(false)
        });
    })
    describe("when ADD_CONTACT action received", () => {
        let state;

        beforeEach(() => {
            state = navigate({shouldClose: false}, {type: ADD_CONTACT, offenderId: '123'})
        })

        it('shouldClose is true', () => {
            expect(state.shouldClose).to.equal(true)
        });
        it('nextPage is addContact', () => {
            expect(state.nextPage).to.equal('addContact')
        });
        it('parameters contain offenderId', () => {
            expect(state.parameters.offenderId).to.equal('123')
        });
    })
    describe("when SHOW_OFFENDER_DETAILS action received", () => {
        let state;

        beforeEach(() => {
            state = navigate({shouldClose: false}, {type: SHOW_OFFENDER_DETAILS, offenderId: '123'})
        })

        it('shouldClose is true', () => {
            expect(state.shouldClose).to.equal(true)
        });
        it('nextPage is showOffenderDetails', () => {
            expect(state.nextPage).to.equal('showOffenderDetails')
        });
        it('parameters contain offenderId', () => {
            expect(state.parameters.offenderId).to.equal('123')
        });
    })
    describe("when LEGACY_SEARCH action received", () => {
        let state;

        beforeEach(() => {
            state = navigate({shouldClose: false}, {type: LEGACY_SEARCH, offenderId: '123'})
        })

        it('shouldClose is true', () => {
            expect(state.shouldClose).to.equal(true)
        });
        it('nextPage is legacySearch', () => {
            expect(state.nextPage).to.equal('legacySearch')
        });
        it('parameters are empty', () => {
            expect(state.parameters).to.be.undefined
        });
    })
    describe("when ADD_NEW_OFFENDER action received", () => {
        let state;

        beforeEach(() => {
            state = navigate({shouldClose: false}, {type: ADD_NEW_OFFENDER, offenderId: '123'})
        })

        it('shouldClose is true', () => {
            expect(state.shouldClose).to.equal(true)
        });
        it('nextPage is addNewOffender', () => {
            expect(state.nextPage).to.equal('addNewOffender')
        });
        it('parameters are empty', () => {
            expect(state.parameters).to.be.undefined
        });
    })

})
