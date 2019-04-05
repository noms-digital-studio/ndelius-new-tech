import { initProgressiveRadioButtons } from './components/progressiveRadioButtons'
import { initFeedbackLinks } from './components/feedbackLinks'
import { initExitLink } from './components/exitLink'
import { reorderErrorMessages } from './components/errorMessages'
import { initReportNavigation } from './components/reportNavigation'
import { initViewDraftLinks } from './components/viewDraftReport'
import { noBackPlease } from './utilities/noBackPlease'
import { initSummaryAnalytics } from './components/summary'

const initAppAll = () => {
  initProgressiveRadioButtons()
  initFeedbackLinks()
  initExitLink()
  reorderErrorMessages()
  initReportNavigation()
  initViewDraftLinks()
  initSummaryAnalytics()
  noBackPlease()
}

export {
  initAppAll
}
