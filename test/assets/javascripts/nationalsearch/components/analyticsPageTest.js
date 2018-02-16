import AnalyticsPage  from './analyticsPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';
import {THIS_YEAR, THIS_WEEK} from '../actions/analytics'

describe('AnalyticsPage component', () => {
    context('on mount', () => {
        it('fetch counts is dispatched', () => {
            const fetchVisitCounts = stub()
            shallow(<AnalyticsPage fetchVisitCounts={fetchVisitCounts} currentTimeRange={THIS_YEAR}/>)

            expect(fetchVisitCounts).to.be.calledOnce
        })
    })

    context('on props received', () => {
        it('fetch counts is dispatched', () => {
            const fetchVisitCounts = stub()
            const page = shallow(<AnalyticsPage fetchVisitCounts={fetchVisitCounts}  currentTimeRange={THIS_YEAR}/>)

            page.setProps({currentTimeRange: THIS_WEEK})

            expect(fetchVisitCounts).to.be.calledTwice
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

