import AnalyticsPage  from './analyticsPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('AnalyticsPage component', () => {
    context('on mount', () => {
        it('fetch counts is dispatched', () => {
            const fetchVisitCounts = stub()
            shallow(<AnalyticsPage fetchVisitCounts={fetchVisitCounts}/>)

            expect(fetchVisitCounts).to.be.called
        })
    })

    describe('rendering', () => {
        let page
        beforeEach(() => {
            page = shallow(<AnalyticsPage fetchVisitCounts={stub()}/>)
        })

        it('displays unique visitor count', () => {
            expect(page.find({description: 'Unique visits'}).exists()).to.be.true
        })
        it('displays all visitor count', () => {
            expect(page.find({description: 'All visits'}).exists()).to.be.true
        })
    })
})

