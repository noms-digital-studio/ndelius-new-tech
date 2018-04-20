import OffenderSearchResults  from './offenderSearchResults'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {offender, restrictedAccessOffender} from '../test-helper'

describe('OffenderSearchResults component', () => {
    context('with no results', () => {
        it('no summaries rendered', () => {
            const results = shallow(<OffenderSearchResults results={noResults()} byProbationArea={[]}/>)
            expect(results.find('Connect(OffenderSearchSummary)')).to.have.length(0)
        })
    })
    context('with one result', () => {
        it('one summary rendered', () => {
            const results = shallow(<OffenderSearchResults results={oneResult()} byProbationArea={[]}/>)
            expect(results.find('Connect(OffenderSearchSummary)')).to.have.length(1)
        })
    })
    context('with many results', () => {
        it('many summaries rendered', () => {
            const results = shallow(<OffenderSearchResults results={twoResults()} byProbationArea={[]}/>)
            expect(results.find('Connect(OffenderSearchSummary)')).to.have.length(2)
        })
    })
    context('with one restricted and one not restricted access offender', () => {
        it('should show a normal summary and a restricted summary', () => {
            const results = shallow(<OffenderSearchResults results={[offender(), restrictedAccessOffender()]} byProbationArea={[]}/>)
            expect(results.find('Connect(OffenderSearchSummary)')).to.have.length(1)
            expect(results.find('Connect(RestrictedOffenderSearchSummary)')).to.have.length(1)

        })
    })
    context('with no byProbationArea', () => {
        it('no filter tables rendered', () => {
            const results = shallow(<OffenderSearchResults results={noResults()} byProbationArea={[]}/>)
            expect(results.find('table.filter')).to.have.length(0)
        })
    })
    context('with byProbationArea', () => {
        it('no filter table is rendered', () => {
            const results = shallow(<OffenderSearchResults results={noResults()} byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}]}/>)
            expect(results.find('table.filter')).to.have.length(1)
        })
    })
    context('with many byProbationArea', () => {
        it('filter row is rendered for each area', () => {
            const results = shallow(<OffenderSearchResults results={noResults()} byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}, {code: 'N02', description: 'Some Other Area', count: 4}]}/>)
            expect(results.find('table.filter tbody tr')).to.have.length(2)
        })
    })
})

const noResults = () => []
const oneResult = () => [offender({offenderId: '123'})]
const twoResults = () => [offender({offenderId: '123'}), offender({offenderId: '234'})]

