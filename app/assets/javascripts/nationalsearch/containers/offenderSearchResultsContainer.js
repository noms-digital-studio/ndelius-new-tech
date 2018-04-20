import { connect } from 'react-redux'
import offenderSearchResults from '../components/offenderSearchResults'

export default connect(
    state => ({
        results: state.search.results,
        byProbationArea: state.search.byProbationArea,
    }),
    () => ({})
)(offenderSearchResults)