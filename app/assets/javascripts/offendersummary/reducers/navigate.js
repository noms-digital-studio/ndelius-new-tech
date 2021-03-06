import {
  NAVIGATE_TO_CLOSE_OFFENDER_SUMMARY,
  NAVIGATE_TO_PREVIOUS_OFFENDER_SUMMARY,
  NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY,
  NAVIGATE_TO_VIEW_OFFENDER_ALIASES,
  NAVIGATE_TO_VIEW_OFFENDER_EVENT,
  NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES,
  NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS
} from '../constants/ActionTypes'

const navigate = (state = { shouldClose: false }, action) => {
  switch (action.type) {
    case NAVIGATE_TO_VIEW_OFFENDER_ALIASES:
      return { shouldClose: true, action: 'viewOffenderAliases', data: action.offenderId }
    case NAVIGATE_TO_VIEW_OFFENDER_ADDRESS_HISTORY:
      return { shouldClose: true, action: 'viewOffenderAddresses', data: action.offenderId }
    case NAVIGATE_TO_VIEW_OFFENDER_PERSONAL_CIRCUMSTANCES:
      return { shouldClose: true, action: 'viewOffenderPersonalCircumstances', data: action.offenderId }
    case NAVIGATE_TO_VIEW_OFFENDER_REGISTRATIONS:
      return { shouldClose: true, action: 'viewOffenderRegistrations', data: action.offenderId }
    case NAVIGATE_TO_VIEW_OFFENDER_EVENT:
      return {
        shouldClose: true,
        action: 'viewEvent',
        data: { offenderId: action.offenderId, eventId: action.eventId }
      }
    case NAVIGATE_TO_PREVIOUS_OFFENDER_SUMMARY:
      return { shouldClose: true, action: 'viewOffenderLegacy', data: action.offenderId }
    case NAVIGATE_TO_CLOSE_OFFENDER_SUMMARY:
      return { shouldClose: true, action: 'close' }
    default:
      return state
  }
}

export default navigate
