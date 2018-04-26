import {removeMyProbationAreas} from './otherAreasFilterContainer'
import {expect} from 'chai';


describe('areaFilterContainer', () => {
    let byProbationArea
    let myProbationAreas
    describe('removeMyProbationAreas', () => {
        context('empty my probation areas ', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 }
                ]
                myProbationAreas = {}

            })

            it('byProbationArea remains unchanged', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas)).to.eql(byProbationArea)
            })
        })
        context('aggregation with filters that exist in myProbationAreas', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 }
                ]
                myProbationAreas = {'N01': 'Area for N01'}
            })

            it('byProbationArea has my provider removed', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas)).to.eql([{code: 'N02', description: 'Area for N02', count: 9 }])
            })
        })
        context('aggregation with no filters', () => {
            beforeEach(() => {
                byProbationArea = [
                ]
                myProbationAreas = {'N03': 'Area for N03', 'N04': 'Area for N04'}
            })

            it('byProbationArea remains unchanged', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas)).to.eql([])
            })
        })
    })

})

