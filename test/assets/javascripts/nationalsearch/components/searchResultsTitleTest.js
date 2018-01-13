import SearchResultsTitle  from './searchResultsTitle'
import {expect} from 'chai';
import { shallow } from 'enzyme';

describe('SearchResultsTitle component', () => {
    context('no results have been searched for', () => {
        it('no text rendered', () => {
            const title = shallow(<SearchResultsTitle results={noResults()} searchTerm={''}/>)
            expect(title.text()).to.equal('')
        })
    })
    context('results have been searched for but none found', () => {
        it('h2 rendered', () => {
            const title = shallow(<SearchResultsTitle results={noResults()} searchTerm={'charlie'}/>)
            expect(title.find('h2')).to.have.length(1)
        })
        it('0 results found rendered', () => {
            const title = shallow(<SearchResultsTitle results={noResults()} searchTerm={'charlie'}/>)
            expect(title.text()).to.equal('0 results found')
        })
    })
    context('results have been searched for with two found', () => {
        it('h2 rendered', () => {
            const title = shallow(<SearchResultsTitle results={twoResults()} searchTerm={'charlie'}/>)
            expect(title.find('h2')).to.have.length(1)
        })
        it('showing 1 to n results found rendered', () => {
            const title = shallow(<SearchResultsTitle results={twoResults()} searchTerm={'charlie'}/>)
            expect(title.text()).to.equal('2 results found, showing 1 to 2')
        })
    })
})

const noResults = () => []
const twoResults = () => [{}, {}]
