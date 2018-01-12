import {ADD_CONTACT} from '../actions/navigate'
import navigate  from './navigateReducer'
import {expect} from 'chai';

describe("navigateReducer", () => {
    describe("when in default state", () => {
        it('should set to not close', () => {
            const state = navigate(undefined, {})
            expect(state.shouldClose).to.equal(false)
        });
    })
    describe("when ADD_CONTACT action", () => {
        it('should set to close', () => {
            const state = navigate({shouldClose: false}, {type: ADD_CONTACT, offenderId: '123'})
            expect(state.shouldClose).to.equal(true)
        });
    })

})
