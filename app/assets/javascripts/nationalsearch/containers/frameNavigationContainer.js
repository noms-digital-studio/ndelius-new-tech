import { connect } from 'react-redux'
import frameNavigation from '../components/frameNavigation.jsx'

const mapStateToProps = state => {
    return {
        navigate: state.navigate
    }
}

const mapDispatchToProps = dispatch => {
    return {}
}

const frameNavigationContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(frameNavigation);

export default frameNavigationContainer