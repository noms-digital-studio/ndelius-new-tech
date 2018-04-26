import {Link} from 'react-router'
import GovUkPhaseBanner from './govukPhaseBanner';

const HelpPage = () => {
    return (<div>
        <div id="root">
            <main id="content">
                <GovUkPhaseBanner basicVersion={true} />
                <div className="key-content">

                    <p className="text-secondary font-xxsmall margin-top medium">&lt; <Link to="search">Back to New Search</Link></p>

                    <h1 className="heading-xlarge">Search tips</h1>

                    <ul>
                        <li className="list-bullet">

                            <h2 className="heading-medium no-margin-bottom">Search by name and date of birth at the same time</h2>
                            <p className="margin-top">For example, <strong>"John Smith 23/06/1986"</strong> - the results will be based on all those search terms.</p>

                        </li>
                    </ul>

                    <p className="margin-top margin-bottom large">
                        <span className="search-hint">
                            <span className="font-medium">John Smith 23/06/1986</span>
                        </span>
                    </p>

                    <ul>
                        <li className="list-bullet">

                            <h2 className="heading-medium no-margin-bottom">Search by town, postcode at the same time as name</h2>
                            <p className="margin-top no-margin-bottom">For example, <strong>"John Smith S1 1AB"</strong> or <strong>"John Smith Sheffield"</strong> and so on. The search box can handle multiple search terms simultaneously.</p>

                        </li>
                    </ul>

                    <p className="margin-top margin-bottom margin-right large pull-left no-pull-mobile">
                        <span className="search-hint">
                            <span className="font-medium">John Smith S1 1AB</span>
                        </span>
                    </p>

                    <p className="font-medium bold search-hint-middle pull-left no-pull-mobile">Or</p>

                    <p className="margin-top margin-bottom margin-right large pull-left no-pull-mobile">
                        <span className="search-hint">
                            <span className="font-medium">John Smith Sheffield</span>
                        </span>
                    </p>

                    <span className="clearfix" />

                    <ul>
                        <li className="list-bullet">

                            <h2 className="heading-medium no-margin-bottom margin-top medium">Include CRN, CRO, PNC, National Insurance and NOMS numbers in your search</h2>
                            <p className="margin-top">Using unique numbers will achieve more accurate results.</p>

                        </li>
                    </ul>

                    <p className="margin-top margin-bottom large">
                        <span className="search-hint">
                            <span className="font-medium">X087946</span>
                        </span>
                    </p>

                    <p>&nbsp;</p>

                    <Link to="search"><span className="font-medium">Previous</span><br/><span className="font-small">New Search</span></Link>

                </div>
            </main>
        </div>
    </div>);
};

export default HelpPage;