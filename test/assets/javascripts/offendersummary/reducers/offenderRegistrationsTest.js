import { OFFENDER_REGISTRATIONS_LOAD_ERROR, RECEIVE_OFFENDER_REGISTRATIONS } from '../constants/ActionTypes'
import offenderRegistrations from './offenderRegistrations'
import { expect } from 'chai'

describe('offenderRegistrationsReducer', () => {
  let state

  describe('when in default state', () => {
    beforeEach(() => {
      state = offenderRegistrations(undefined, { type: '"@@redux/INIT"' })
    })

    it('fetching is true', () => {
      expect(state.fetching).to.equal(true)
    })
    it('load error not set', () => {
      expect(state.loadError).to.equal(false)
    })
  })

  describe('when RECEIVE_OFFENDER_REGISTRATIONS action received', () => {
    beforeEach(() => {
      state = offenderRegistrations({ fetching: true, loadError: true }, {
        type: RECEIVE_OFFENDER_REGISTRATIONS,
        registrations: [{ type: 'bad' }]
      })
    })
    it('details set', () => {
      expect(state.registrations).to.have.length(1)
    })
    it('fetching toggled off', () => {
      expect(state.fetching).to.equal(false)
    })
    it('load error is cleared', () => {
      expect(state.loadError).to.equal(false)
    })
  })

  describe('when OFFENDER_REGISTRATIONS_LOAD_ERROR action received', () => {
    beforeEach(() => {
      state = offenderRegistrations({ fetching: true }, {
        type: OFFENDER_REGISTRATIONS_LOAD_ERROR,
        error: new Error('Boom!')
      })
    })
    it('load error set', () => {
      expect(state.loadError).to.equal(true)
    })
    it('fetching toggled off', () => {
      expect(state.fetching).to.equal(false)
    })
  })
})
