import AreaFilter  from './areaFilter'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('AreaFilter component', () => {
    let addAreaFilter;
    let removeAreaFilter;
    let search;

    beforeEach(() => {
        addAreaFilter = stub()
        removeAreaFilter = stub()
        search = stub()
    })
    describe ('area rendering', () => {
        context('with no byProbationArea', () => {
            it('no filter tables rendered', () => {
                const filter = shallow(<AreaFilter
                    searchTerm='Mr Bean'
                    byProbationArea={[]}
                    areaFilter={[]}
                    addAreaFilter={addAreaFilter}
                    removeAreaFilter={removeAreaFilter}
                    search={search}
                />)
                expect(filter.find('table.filter')).to.have.length(0)
            })
        })
        context('with byProbationArea', () => {
            it('filter table is rendered', () => {
                const filter = shallow(<AreaFilter
                    searchTerm='Mr Bean'
                    byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}]}
                    areaFilter={[]}
                    addAreaFilter={addAreaFilter}
                    removeAreaFilter={removeAreaFilter}
                    search={search}
                />)
                expect(filter.find('table.filter')).to.have.length(1)
            })
        })
        context('with many byProbationArea', () => {
            it('filter row is rendered for each area', () => {
                const filter = shallow(<AreaFilter
                    searchTerm='Mr Bean'
                    byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}, {code: 'N02', description: 'Some Other Area', count: 4}]}
                    areaFilter={[]}
                    addAreaFilter={addAreaFilter}
                    removeAreaFilter={removeAreaFilter}
                    search={search}
                />)
                expect(filter.find('table.filter tbody tr')).to.have.length(2)
            })
        })
    })

    describe ('area selection rendering', () => {
        let filter;
        context('when areaFilter empty', () => {
            beforeEach(() => {
                filter = shallow(<AreaFilter
                    searchTerm='Mr Bean'
                    byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}]}
                    areaFilter={[]}
                    addAreaFilter={addAreaFilter}
                    removeAreaFilter={removeAreaFilter}
                    search={search}
                />)

            })
            it('0 selected in selection hint text', () => {
                expect(filter.find('#selected').text()).to.equal('0 selected')
            })
            it('no boxes checked', () => {
                expect(filter.find({checked: true})).to.have.length(0)
            })
        })
        context('when areaFilter has one item', () => {
            beforeEach(() => {
                filter = shallow(<AreaFilter
                    searchTerm='Mr Bean'
                    byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}]}
                    areaFilter={['N01']}
                    addAreaFilter={addAreaFilter}
                    removeAreaFilter={removeAreaFilter}
                    search={search}
                />)

            })
            it('1 selected in selection hint text', () => {
                expect(filter.find('#selected').text()).to.equal('1 selected')
            })
            it('area checkbox should be checked', () => {
                expect(filter.find({value: 'N01'}).prop('checked')).to.be.true
            })
        })
        context('when areaFilter has many item', () => {
            beforeEach(() => {
                filter = shallow(<AreaFilter
                    searchTerm='Mr Bean'
                    byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}, {code: 'N02', description: 'Some Other Area', count: 4}, {code: 'N03', description: 'Some Other Area', count: 4}]}
                    areaFilter={['N01', 'N03']}
                    addAreaFilter={addAreaFilter}
                    removeAreaFilter={removeAreaFilter}
                    search={search}
                />)

            })
            it('2 selected in selection hint text', () => {
                expect(filter.find('#selected').text()).to.equal('2 selected')
            })
        })
    })

    describe('selection filter areas', () => {
        let filter;
        beforeEach(() => {
            filter = shallow(<AreaFilter
                searchTerm='Mr Bean'
                byProbationArea={[{code: 'N01', description: 'N01 Area', count: 67}, {code: 'N02', description: 'N02 Area', count: 4}, {code: 'N03', description: 'N03 Area', count: 4}]}
                areaFilter={['N01', 'N03']}
                addAreaFilter={addAreaFilter}
                removeAreaFilter={removeAreaFilter}
                search={search}
            />)

        })
        describe('when filter no currently checked', () => {
            it('call addAreaFilter with code', () => {
                filter.find({value: 'N02'}).simulate('change', {target: {value: 'N02'}});
                expect(addAreaFilter).to.be.calledWith('N02', 'N02 Area');
            })
        })
        describe('when filter is currently checked', () => {
            it('call removeAreaFilter with code', () => {
                filter.find({value: 'N01'}).simulate('change', {target: {value: 'N01'}});
                expect(removeAreaFilter).to.be.calledWith('N01');
            })
        })
    })
    describe('componentWillReceiveProps', () => {
        let filter;
        const currentAreaFilter = ['N01', 'N03'];
        beforeEach(() => {
            filter = shallow(<AreaFilter
                searchTerm='Mr Bean'
                byProbationArea={[{code: 'N01', description: 'Some Area', count: 67}, {code: 'N02', description: 'Some Other Area', count: 4}, {code: 'N03', description: 'Some Other Area', count: 4}]}
                areaFilter={currentAreaFilter}
                addAreaFilter={addAreaFilter}
                removeAreaFilter={removeAreaFilter}
                search={search}
            />)

        })
        context('when area filter changes', () => {
            it('calls search', () => {
                filter.setProps({ areaFilter: ['N01'] });
                expect(search.calledOnce).to.equal(true);
                expect(search.getCall(0).args[0]).to.equal('Mr Bean');
                expect(search.getCall(0).args[1]).to.eql(['N01']);
            })
        })
        context('when some thing else changes', () => {
            it('calls nothing', () => {
                filter.setProps({ areaFilter: currentAreaFilter });
                expect(search.calledOnce).to.equal(false);
            })

        })
    })
})


