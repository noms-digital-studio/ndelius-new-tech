import OffenderSearch  from './offenderSearch'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('OffenderSearch component', () => {
    context('search term typed in', () => {
        it('search callback function called with search term', () => {
            const search = stub()
            const searchBox = shallow(<OffenderSearch search={search} searchTerm={'Mr Bean'}/>)

            searchBox.find('input').simulate('change', {target: {value: 'Mr Beans'}});

            expect(search).to.be.calledWith('Mr Beans');
        })
    })
})

