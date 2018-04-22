import OffenderSearchSummary from '../containers/offenderSearchSummaryContainer';
import RestrictedOffenderSearchSummary from '../containers/restrictedOffenderSearchSummaryContainer'
import SearchResultsTitle from '../containers/searchResultsTitleContainer';
import PageSelection from '../containers/pageSelectionContainer';
import AreaFilter from '../containers/areaFilterContainer';

import PropTypes from "prop-types";

const OffenderSearchResults = ({results}) => (
    <div className='padded mobile-pad' id='offender-results'>
        <SearchResultsTitle/>

        <div className='grid-row'>
            <div className='column-one-third'>
                <AreaFilter/>
            </div>
            <div className='column-two-thirds'>
                <ul>
                    {results.map(offenderSummary => (
                        renderSummary(offenderSummary)
                    ))}
                </ul>
                <PageSelection/>
            </div>
        </div>
    </div>
);

OffenderSearchResults.propTypes = {
    results: PropTypes.array.isRequired
};

const renderSummary = offenderSummary => {
    if (offenderSummary.accessDenied) {
        return <RestrictedOffenderSearchSummary offenderSummary={offenderSummary} key={offenderSummary.offenderId}/>
    }
    return <OffenderSearchSummary offenderSummary={offenderSummary} key={offenderSummary.offenderId}/>
}

export default OffenderSearchResults;