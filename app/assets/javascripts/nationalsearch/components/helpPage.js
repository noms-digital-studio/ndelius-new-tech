import {Link} from 'react-router'
import GovUkPhaseBanner from './govukPhaseBanner';

const HelpPage = () => {
    return (<div>
        <div id="root">
            <main id="content">
                <GovUkPhaseBanner/>
                <div className="key-content">

                    <h1 className="heading-xlarge">Search tips</h1>

                    <h2 className="heading-medium">Search by name and date of birth at the same time</h2>

                    <p>For example, <strong>"John Smith 23/06/1986"</strong> - the results will be based on all those
                        search terms.</p>

                    <p className="margin-top margin-bottom large">
                        <span className="search-hint">
                            <span className="font-medium">John Smith 23/06/1986</span>
                        </span>
                    </p>

                    <h2 className="heading-medium">Search by town, postcode at the same time as name</h2>

                    <p>For example, <strong>"John Smith S1 1AB"</strong> or <strong>"John Smith Sheffield"</strong> and
                        so on. The search box can handle multiple search terms simultaneously.</p>

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

                    <h2 className="heading-medium">Include CRN, CRO, PNC, National Insurance and NOMS numbers in your
                        search</h2>

                    <p>Using unique numbers will achieve more accurate results.</p>

                    <p className="margin-top margin-bottom large">
                        <span className="search-hint">
                            <span className="font-medium">X087946</span>
                        </span>
                    </p>

                    <Link to="search">Previous<br/>New Search</Link>

                </div>
            </main>
        </div>
    </div>);
};

export default HelpPage;