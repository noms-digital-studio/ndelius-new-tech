import { connect } from 'react-redux'
import markableText from '../components/markableText'

const mapStateToProps = (state) => {
    return {
        searchTerm: state.search.searchTerm
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