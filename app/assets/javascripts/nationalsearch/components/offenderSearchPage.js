import OffenderSearchResults from '../containers/offenderSearchResultsContainer';
import OffenderSearch from '../containers/offenderSearchContainer';
import FrameNavigation from '../containers/frameNavigationContainer';
import AddNewOffenderLink from '../containers/addNewOffenderLinkContainer';
import Suggestions from '../containers/suggestionsContainer';
import GovUkPhaseBanner from './govukPhaseBanner';
import SearchFooter from './searchFooter';
import PropTypes from 'prop-types';

const OffenderSearchPage = ({firstTimeIn, showWelcomeBanner, reloadRecentSearch}) => {

    if (firstTimeIn) {
        reloadRecentSearch();
    }

    return (
        <div>
            <div id="root">
                <main id="content">
                    <GovUkPhaseBanner/>
                    <div className="govuk-box-highlight blue">
                        <div className="key-content search relative">

                            <div className="padding-top padding-bottom search">
                                <div className="grid-row">
                                    <div className="column-two-thirds">
                                        <h1 className="heading-xlarge margin-bottom medium no-margin-top">Search for an
                                            offender</h1>
                                    </div>
                                    <div className="column-one-third align-right">
                                        <AddNewOffenderLink tabIndex="1"/>
                                    </div>
                                </div>

                                <OffenderSearch/>
                                <Suggestions/>

                            </div>

                            <div className="national-search-help">
                                <p className="no-margin">Not found what you are looking for?</p>
                                <a className="font-medium bold clickable">Narrow down your search</a>
                            </div>
                        </div>
                    </div>
                    <div className="key-content">
                        {showWelcomeBanner && <SearchFooter/>}
                        {!showWelcomeBanner && <OffenderSearchResults/>}
                    </div>
                </main>
            </div>
            <FrameNavigation/>
        </div>
    );
};

OffenderSearchPage.propTypes = {
    firstTimeIn: PropTypes.bool.isRequired,
    showWelcomeBanner: PropTypes.bool.isRequired,
    reloadRecentSearch: PropTypes.func.isRequired
};

export default OffenderSearchPage;