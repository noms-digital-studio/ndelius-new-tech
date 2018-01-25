import LegacySearchLink from "./legacySearchLink";

export default () => (
    <div>
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
    </div>);