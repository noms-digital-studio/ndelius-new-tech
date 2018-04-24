import {combineWithProbationAreaFilter} from './areaFilterContainer'
import {expect} from 'chai';


describe('areaFilterContainer', () => {
    let byProbationArea
    let probationAreasFilter
    describe('combineWithProbationAreaFilter', () => {
        context('aggregation with no filters', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 }
                ]
                probationAreasFilter = {}

            })

            it('byProbationArea remains unchanged', () => {
                expect(combineWithProbationAreaFilter(byProbationArea, probationAreasFilter)).to.eql(byProbationArea)
            })
        })
        context('aggregation with filters that exist in byProbationArea', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 }
                ]
                probationAreasFilter = {'N01': 'Area for N01'}
            })

            it('byProbationArea remains unchanged', () => {
                expect(combineWithProbationAreaFilter(byProbationArea, probationAreasFilter)).to.eql(byProbationArea)
            })
        })
        context('aggregation with filters that do not exist in byProbationArea', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 }
                ]
                probationAreasFilter = {'N03': 'Area for N03', 'N04': 'Area for N04'}
            })

            it('filters are added at end byProbationArea with zero counts', () => {
                expect(combineWithProbationAreaFilter(byProbationArea, probationAreasFilter)).to.eql([
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 },
                    {code: 'N03', description: 'Area for N03', count: 0 },
                    {code: 'N04', description: 'Area for N04', count: 0 }

                ])
            })
        })
    })

})

