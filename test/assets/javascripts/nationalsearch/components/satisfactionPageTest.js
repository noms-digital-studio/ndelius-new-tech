import SatisfactionPage from './satisfactionPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('SatisfactionPage component', () => {
    context('on mount', () => {
        it('fetch satisfaction counts is dispatched', () => {
            const fetchSatisfactionCounts = stub();
            const changeYear = stub();
            shallow(<SatisfactionPage fetchSatisfactionCounts={fetchSatisfactionCounts} changeYear={changeYear}/>);

            expect(fetchSatisfactionCounts).to.be.calledOnce
        })
    })

    context('refresh button clicked', () => {
        it('fetch satisfaction counts is dispatched for mount and click', () => {
            const fetchSatisfactionCounts = stub();
            const changeYear = stub();
            const page = shallow(<SatisfactionPage fetchSatisfactionCounts={fetchSatisfactionCounts} changeYear={changeYear}/>)

            page.find({type: 'button'}).simulate('click')

            expect(fetchSatisfactionCounts).to.be.calledTwice
        })
    })


    describe('rendering', () => {
        let page
        beforeEach(() => {
            page = shallow(<SatisfactionPage fetchSatisfactionCounts={stub()} changeYear={stub()}/>)
        });

        it('displays weekly satisfaction counts', () => {
            expect(page.find('#description').text()).to.equal('Weekly Satisfaction Counts');
        });
    })
})


