import LegacySearchLink from "../containers/legacySearchLinkContainer";

const SearchFooter = () => (
    <div className="govuk-interrupt margin-top large">
        <h3 className="heading-medium no-margin-top">Find an offender by using any combination of:</h3>

        <ul className="list-bullet margin-top medium">
            <li>name and date of birth</li>
            <li>address and previous addresses</li>
            <li>CRN, PNC and National Insurance numbers</li>
            <li>aliases and other recorded dates of birth</li>
        </ul>

        <div className="margin-top medium">
            <span>You can still access the <LegacySearchLink tabIndex="1">previous version of search</LegacySearchLink>.</span>
        </div>
    </div>);

export default SearchFooter