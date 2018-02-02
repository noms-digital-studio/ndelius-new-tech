import LegacySearchLink from "../containers/legacySearchLinkContainer";

const SearchFooter = () => (
    <div>
        <h3 className="heading-medium">Use this <strong className="phase-tag">ALPHA</strong> service to search for offender's:</h3>

        <ul className="list-bullet margin-top medium">
            <li>names and dates of birth</li>
            <li>addresses and previous addresses</li>
            <li>identification numbers such as CRN, PNC, and National insurance</li>
            <li>aliases and dates of birth</li>
        </ul>

        <div className="margin-top medium">&nbsp;</div>

        <div>
            <span>You can access the <LegacySearchLink>previous</LegacySearchLink> search here.</span>
        </div>
    </div>);

export default SearchFooter