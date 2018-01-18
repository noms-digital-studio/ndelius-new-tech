import { connect } from 'react-redux'
import { legacySearch } from '../actions/navigate'
import legacySearchLink from '../components/legacySearchLink'

const mapStateToProps = () => {
    return {}
};

const mapDispatchToProps = (dispatch) => {
    return {
        legacySearch: () => {
            dispatch(legacySearch())
        }
    }
};

const legacySearchLinkContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(legacySearchLink);

export default legacySearchLinkContainer