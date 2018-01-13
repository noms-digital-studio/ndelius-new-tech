import OffenderSearchSummary, {Address}  from './offenderSearchSummary'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {offender} from '../test-helper'


describe('OffenderSearchSummary component', () => {
    it('should render offender summary elements', () => {

        const offenderSummary = offender()
        const summary = shallow(<OffenderSearchSummary {...offenderSummary} />)


        expect(summary.find('Connect(OffenderSummaryTitle)')).to.have.length(1)
        expect(summary.find('CurrentOffender')).to.have.length(1)
        expect(summary.find('AliasList')).to.have.length(1)
        expect(summary.find('PreviousSurname')).to.have.length(1)
        expect(summary.find('AddressList')).to.have.length(1)
        expect(summary.find('Connect(AddContactLink)')).to.have.length(1)
    })
})

describe('Address component', () => {
    const extractLines = address => address.find('Connect(MarkableText)').map(text => text.prop('text'))

    context('with all address lines', () => {
        it('all lines rendered with address number concatenated with street name', () => {
            const address = shallow(<Address address={{
                buildingName: 'Big Building',
                addressNumber: '99',
                streetName: 'High Street',
                town: 'Sheffield',
                county: 'South Yorkshire',
                postcode: 'S1 2BX',
            }}/>)

            const lines = extractLines(address)

            expect(lines).to.eql(
                ['Big Building', '99 High Street', 'Sheffield', 'South Yorkshire', 'S1 2BX']
            )
        })
    })
    context('with many address lines missing', () => {
        it('lines removed but with address number concatenated with street name', () => {
            const address = shallow(<Address address={{
                buildingName: '',
                addressNumber: '99',
                streetName: 'High Street',
                town: null,
                postcode: 'S1 2BX',
            }}/>)

            const lines = extractLines(address)

            expect(lines).to.eql(
                ['99 High Street', 'S1 2BX']
            )
        })
    })
})
