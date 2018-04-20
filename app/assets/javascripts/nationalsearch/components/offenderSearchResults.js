import OffenderSearchSummary from '../containers/offenderSearchSummaryContainer';
import RestrictedOffenderSearchSummary from '../containers/restrictedOffenderSearchSummaryContainer'
import SearchResultsTitle from '../containers/searchResultsTitleContainer';
import PageSelection from '../containers/pageSelectionContainer';

import PropTypes from "prop-types";

const OffenderSearchResults = ({results, byProbationArea}) => (
    <div className='padded mobile-pad' id='offender-results'>
        <SearchResultsTitle/>

        <div className='grid-row'>
            <div className='column-one-third'>
                <div className='js-stick-at-top-when-scrolling'>
                    {shouldDisplayFilter(byProbationArea) &&
                    <table className='filter'>
                        <thead>
                        <tr>
                            <th>
                                <span className='bold-medium'>Provider</span><br/>
                                <span className='font-small'>0 selected</span>
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        {byProbationArea.map(probationArea => (
                            renderProbationArea(probationArea)
                        ))}
                        </tbody>
                    </table>
                    }
                </div>
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
    results: PropTypes.array.isRequired,
    byProbationArea: PropTypes.arrayOf(PropTypes.shape({
        code: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        count: PropTypes.number.isRequired
    })).isRequired
};

const renderSummary = offenderSummary => {
    if (offenderSummary.accessDenied) {
        return <RestrictedOffenderSearchSummary offenderSummary={offenderSummary} key={offenderSummary.offenderId}/>
    }
    return <OffenderSearchSummary offenderSummary={offenderSummary} key={offenderSummary.offenderId}/>
}

const renderProbationArea = probationArea => ( <tr key={probationArea.code}><td className='multiple-choice margin-top'><input type='checkbox' value={probationArea.code} id={probationArea.code}/><label htmlFor={probationArea.code}>{probationArea.description} ({probationArea.count})</label></td></tr>)
const shouldDisplayFilter = byProbationArea => byProbationArea.length > 0
export default OffenderSearchResults;