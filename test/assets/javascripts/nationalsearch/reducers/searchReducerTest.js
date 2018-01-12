import {CLEAR_RESULTS, REQUEST_SEARCH, SEARCH_RESULTS} from '../actions/search'
import search from './searchReducer'
import {expect} from 'chai';

describe("searchReducer", () => {
    let state;

    describe("when in default state", () => {

        beforeEach(() => {
            state = search(undefined, {type: '"@@redux/INIT"'})
        })

        it('searchTerm will be blank', () => {
            expect(state.searchTerm).to.equal('')
        });
        it('results will be empty', () => {
            expect(state.results).to.be.empty
        });
    })
    describe("when REQUEST_SEARCH action received", () => {

        beforeEach(() => {
            state = search({searchTerm: 'Mr Bean', results: someResults()}, {
                type: REQUEST_SEARCH,
                searchTerm: 'John Smith'
            })
        })

        it('searchTerm is set to value', () => {
            expect(state.searchTerm).to.equal('John Smith')
        });
        it('results is kept at existing value', () => {
            expect(state.results).to.eql(someResults())
        });
    })
    describe("when SEARCH_RESULTS action received", () => {

        context('when searchTerm matches from request action', () => {
            beforeEach(() => {
                state = search(
                    {searchTerm: 'Mr Bean', results: someResults()},
                    {type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: emptyResults()})
            })
            it('results are replaced with new results', () => {
                expect(state.results).to.eql(emptyResults())
            });
        })
        context('when searchTerm does not match from request action', () => {
            beforeEach(() => {
                state = search(
                    {searchTerm: 'Mr Fancy', results: someResults()},
                    {type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: emptyResults()})
            })
            it('existing results are kept and new results discarded', () => {
                expect(state.results).to.eql(someResults())
            });
        })
        describe('search results copying', () => {
            beforeEach(() => {
                state = search(
                    {searchTerm: 'Mr Bean', results: emptyResults()},
                    {type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: someResults({
                            offenders: [
                                {
                                    offenderId: '99',
                                    firstName: 'Rose',
                                    surname: 'Blakesmith',
                                    crn: 'DL999',
                                    dateOfBirth: '19-07-1965',
                                    risk: 'Red',
                                    currentOffender: false,
                                    gender: 'Female',
                                    age: 23,
                                    addresses: [],
                                    aliases: []
                                }
                            ]
                        })})
            })
            it('core attributes copied', () => {
                expect(state.results[0].offenderId).to.equal('99')
                expect(state.results[0].firstName).to.equal('Rose')
                expect(state.results[0].surname).to.equal('Blakesmith')
                expect(state.results[0].crn).to.equal('DL999')
                expect(state.results[0].dateOfBirth).to.equal('19-07-1965')
                expect(state.results[0].risk).to.equal('Red')
                expect(state.results[0].currentOffender).to.equal(false)
                expect(state.results[0].gender).to.equal('Female')
                expect(state.results[0].age).to.equal(23)
            });
        })
        describe("address filtering", () => {
            context("searchTerm has no matches in address", () => {
                beforeEach(() => {
                    state = search(
                        {searchTerm: 'Mr Bean', results: someResults()},
                        {type: SEARCH_RESULTS, searchTerm: 'Mr Bean', results: someSingleResultWithAddresses([
                                {
                                    addressNumber: '1',
                                    streetName: 'High Street',
                                    town: 'Sheffield',
                                    postcode: 'S1 2BX'
                                },
                                {
                                    addressNumber: '1',
                                    streetName: 'Sheffield Road',
                                    town: 'Leeds',
                                    postcode: 'LS1 2BX'
                                },
                                {
                                    addressNumber: '1',
                                    streetName: 'Ticky Street',
                                    town: 'London',
                                    postcode: 'SW11 2BX'

                                }
                            ])})
                })

                it('all addresses are filtered out', () => {
                    expect(state.results[0].addresses).to.have.lengthOf(0)
                });
            })
            context("searchTerm has some matches in address", () => {
                beforeEach(() => {
                    state = search(
                        {searchTerm: 'Mr Bean sheffield', results: someResults()},
                        {type: SEARCH_RESULTS, searchTerm: 'Mr Bean sheffield', results: someSingleResultWithAddresses([
                                {
                                    buildingName: 'Sloppy Buildings',
                                    addressNumber: '1',
                                    streetName: 'High Street',
                                    town: 'Sheffield',
                                    county: 'South Yorkshire',
                                    postcode: 'S1 2BX'
                                },
                                {
                                    addressNumber: '1',
                                    streetName: 'Sheffield Road',
                                    town: 'Leeds',
                                    postcode: 'LS1 2BX'
                                },
                                {
                                    addressNumber: '1',
                                    streetName: 'Ticky Street',
                                    town: 'London',
                                    postcode: 'SW11 2BX'

                                }
                            ])})
                })

                it('non matching addresses are filtered out', () => {
                    expect(state.results[0].addresses).to.have.lengthOf(2)
                });
                it('all addresses attributes copied from matching address', () => {
                    expect(state.results[0].addresses[0].buildingName).to.equal('Sloppy Buildings')
                    expect(state.results[0].addresses[0].addressNumber).to.equal('1')
                    expect(state.results[0].addresses[0].streetName).to.equal('High Street')
                    expect(state.results[0].addresses[0].town).to.equal('Sheffield')
                    expect(state.results[0].addresses[0].county).to.equal('South Yorkshire')
                    expect(state.results[0].addresses[0].postcode).to.equal('S1 2BX')
                });
            })
        })
        describe("alias filtering", () => {
            context("searchTerm has no matches in alias", () => {
                beforeEach(() => {
                    state = search(
                        {searchTerm: 'Trevor', results: someResults()},
                        {type: SEARCH_RESULTS, searchTerm: 'Trevor', results: someSingleResultWithAliases([
                                {
                                    firstName: 'Bean',
                                    surname: 'Bland',
                                },
                                {
                                    firstName: 'Bobby',
                                    surname: 'Bean',
                                },
                                {
                                    firstName: 'Tommy',
                                    surname: 'Tibbs',
                                }
                            ])})
                })

                it('all aliases are filtered out', () => {
                    expect(state.results[0].aliases).to.have.lengthOf(0)
                });
            })
            context("searchTerm has some matches in alias", () => {
                beforeEach(() => {
                    state = search(
                        {searchTerm: 'bean', results: someResults()},
                        {type: SEARCH_RESULTS, searchTerm: 'bean', results: someSingleResultWithAliases([
                                {
                                    firstName: 'Bean',
                                    surname: 'Bland',
                                },
                                {
                                    firstName: 'Bobby',
                                    surname: 'Bean',
                                },
                                {
                                    firstName: 'Tommy',
                                    surname: 'Tibbs',
                                }
                            ])})
                })

                it('non matching aliases are filtered out', () => {
                    expect(state.results[0].aliases).to.have.lengthOf(2)
                });
                it('all aliases attributes copied from matching alias', () => {
                    expect(state.results[0].aliases[0].firstName).to.equal('Bean')
                    expect(state.results[0].aliases[0].surname).to.equal('Bland')
                });
            })
        })
        describe("previous surname filtering", () => {
            context("searchTerm has no matches in previous surname", () => {
                beforeEach(() => {
                    state = search(
                        {searchTerm: 'trevor', results: someResults()},
                        {type: SEARCH_RESULTS, searchTerm: 'trevor', results: someSingleResultWithPreviousSurname('Bobby')})
                })

                it('previous surname is set to null', () => {
                    expect(state.results[0].previousSurname).to.equal(null);
                });
            })
            context("previous surname not present in results", () => {
                beforeEach(() => {
                    state = search(
                        {searchTerm: 'trevor', results: someResults()},
                        {type: SEARCH_RESULTS, searchTerm: 'trevor', results: someSingleResultWithPreviousSurname()})
                })

                it('previous surname is set to null', () => {
                    expect(state.results[0].previousSurname).to.equal(null);
                });
            })
            context("searchTerm has some matches in previous surname", () => {
                beforeEach(() => {
                    state = search(
                        {searchTerm: 'trevor', results: someResults()},
                        {type: SEARCH_RESULTS, searchTerm: 'trevor', results: someSingleResultWithPreviousSurname('Trevor')})
                })

                it('previous surname is copied', () => {
                    expect(state.results[0].previousSurname).to.equal('Trevor')
                });
            })
        })
    })
    describe("when CLEAR_RESULTS action received", () => {

        beforeEach(() => {
            state = search({searchTerm: 'Mr Bean', results: [{aResult: {}}]}, {type: CLEAR_RESULTS})
        })

        it('searchTerm will be blank', () => {
            expect(state.searchTerm).to.equal('')
        });
        it('results will be empty', () => {
            expect(state.results).to.be.empty
        });
    })

})

function someResults(results = {}) {
    return Object.assign({
        offenders: [{
            offenderId: '123',
            firstName: 'John',
            surname: 'Smith',
            addresses: [],
            aliases: []
        }]
    }, results);
}

function emptyResults() {
    return someResults().offenders = [];
}

function someSingleResultWithAddresses(addresses) {
    const results = someResults();
    results.offenders = [results.offenders[0]];
    results.offenders[0].addresses = addresses;
    return results;
}

function someSingleResultWithAliases(aliases) {
    const results = someResults();
    results.offenders = [results.offenders[0]];
    results.offenders[0].aliases = aliases;
    return results;
}

function someSingleResultWithPreviousSurname(previousSurname) {
    const results = someResults();
    results.offenders = [results.offenders[0]];
    if (previousSurname) {
        results.offenders[0].previousSurname = previousSurname;
    } else {
        delete results.offenders[0].previousSurname;
    }
    return results;
}

