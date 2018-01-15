import PageSelection  from './pageSelection'
import {expect} from 'chai';
import { shallow } from 'enzyme';

describe('PageSelection component', () => {
    context('no results have been found', () => {
        it('no text rendered', () => {
            const title = shallow(<PageSelection pageSize={10} page={0} total={0}/>)
            expect(title.text()).to.equal('')
        })
    })
})

