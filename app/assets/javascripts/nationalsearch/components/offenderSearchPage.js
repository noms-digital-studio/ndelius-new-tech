import OffenderSearchResults from '../containers/offenderSearchResultsContainer';
import OffenderSearch from '../containers/offenderSearchContainer';
import FrameNavigation from '../containers/frameNavigationContainer';
import AddNewOffenderLink from '../containers/addNewOffenderLinkContainer';
import Suggestions from '../containers/suggestionsContainer';
import GovUkPhaseBanner from './govukPhaseBanner';
import LegacySearchLink from "./legacySearchLink";

export default () => (
    <div>
        <noscript>You need to enable JavaScript to run this app.</noscript>
        <div id="root">
            <main id="content">
                <GovUkPhaseBanner/>
                <div>
                    <div className="govuk-box-highlight blue">
                        <h1 className="heading-large no-margin-top margin-bottom medium">Search for an offender</h1>
                        <OffenderSearch/>
                        <Suggestions/>
                        <p className="bold margin-top medium no-margin-bottom">Can't find who you are looking for? <AddNewOffenderLink/></p></div>
                </div>
                <div className="padded mobile-pad">
                    <OffenderSearchResults/>
                </div>
                <div className="margin-left padding-left">
                    <h3 className="heading-medium">Use this <strong className="phase-tag">ALPHA</strong> service to search for:</h3>
                    <ul className="list-bullet margin-top medium">
                        <li>Offender names and dates of birth</li>
                        <li>Offender addresses and previous addresses</li>
                        <li>Identification numbers such as CRN, PNC, and National insurance</li>
                        <li>Offender aliases and dates of birth</li>
                    </ul>
                    <div class="margin-top medium">&nbsp;</div>
                    <div>
                        <span>You can access the <LegacySearchLink>previous version</LegacySearchLink> of the search here.</span>
                    </div>
                </div>
            </main>
        </div>
        <FrameNavigation/>
    </div>);

