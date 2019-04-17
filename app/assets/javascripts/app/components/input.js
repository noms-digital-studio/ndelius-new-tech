import { nodeListForEach } from '../utilities/nodeListForEach'
import { debounce } from '../utilities/debounce'
import { autoSaveProgress } from '../helpers/saveProgressHelper'

const initInputs = () => {

  nodeListForEach(document.querySelectorAll('input:not([type="hidden"])'), $input => {
    if ($input.type === 'checkbox' || $input.type === 'radio') {
      $input.addEventListener('click', () => {
        autoSaveProgress($input)
      })
    } else {
      $input.addEventListener('blur', () => {
        autoSaveProgress($input)
      })
      $input.addEventListener('keyup', debounce(() => {
        autoSaveProgress($input)
      }))
    }
  })
}

export {
  initInputs
}