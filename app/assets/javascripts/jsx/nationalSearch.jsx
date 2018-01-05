let data = {
    results: [],
    searchTerm: ""
};

const performSearch = _.debounce((searchTerm) => {

    $.getJSON('spellcheck/' + searchTerm, data => {
        data = {results: data, searchTerm: searchTerm};

        ReactDOM.render(
            <OffenderSearchPage search={data.searchTerm} results={data.results} />,
            document.getElementById('content')
        );
    });
}, 500);

class ResultsRow extends React.Component {

    render() {
        return (
            <li>
                <span>{this.props.mistake}</span>
                <ul>
                    {this.props.suggestions.map(suggestion => (
                        <li>{suggestion}</li>
                    ))}
                </ul>
            </li>
        );
    }
}

class ResultsGrid extends React.Component {

    render() {
        return (
            <ul>
                {this.props.results.map(result => (
                    <ResultsRow {...result} />
                ))}
            </ul>
        );
    }
}

class OffenderSearch extends React.Component {

    render() {
        let { searchTerm } = this.props;

        return (
            <div>
                <input className="form-control padded" value={searchTerm} onChange={(event) => performSearch(event.target.value)} placeholder="Find names, addresses, date of birth, CRN and more..." />
            </div>
        );
    }
}

const Banner = () => (
    <div className="phase-banner mobile-pad no-cell"><p><strong className="phase-tag">PROTOTYPE</strong><span>This is an experimental service – your <a href="feedback">feedback</a> will help us to improve it.</span></p></div>
);


class OffenderSearchPage extends React.Component {
    render() {
        let { results, searchTerm } = this.props;

        return (
            <div>
                <noscript>You need to enable JavaScript to run this app.</noscript>
                <div id="root">
                    <main id="content">
                        <Banner/>
                        <div>
                            <div className="govuk-box-highlight blue">
                                <h1 className="heading-large no-margin-top margin-bottom medium">Search for an offender</h1>
                                <form className="padding-left-right"><OffenderSearch searchTerm={searchTerm}/></form>
                                <p className="bold margin-top medium no-margin-bottom">Can't find who you are looking for? <a className="clickable white">Add a new offender</a></p></div>
                        </div>
                        <div className="padded mobile-pad">
                            <ResultsGrid results={results} />
                        </div>
                    </main>
                </div>
            </div>);
    }

}


ReactDOM.render(
    <OffenderSearchPage search={data.searchTerm} results={data.results} />,
    document.getElementById('content')
);
