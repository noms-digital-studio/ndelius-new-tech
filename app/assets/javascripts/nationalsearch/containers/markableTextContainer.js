import { connect } from 'react-redux'
import markableText from '../components/markableText.jsx'

const mapStateToProps = (state) => {
    return {
        searchTerm: state.searchTerm
    }
};

const mapDispatchToProps = () => {
    return {}
};

const markableTextContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(markableText);

export default markableTextContainer