import PropTypes from 'prop-types'
import AddContactLink from '../containers/addContactLinkContainer'

const RestrictedOffenderSearchSummary = ({offenderSummary, showOffenderDetails}) => (
    <li>
        <div className="offenderDetailsRow clearfix">
            <div className='offenderImageContainer'>
                <img className="offenderImage" src='assets/images/NoPhoto@2x.png'/>
            </div>
            <div role='group' className='panel panel-border-narrow offender-summary'>
                <p>
                    <span>
                        <a className='heading-large no-underline offender-summary-title' onClick={() => showOffenderDetails(offenderSummary.offenderId, offenderSummary.rankIndex, {})}>
                            <span>Restricted access</span>
                        </a>
                    </span>
                </p>
                <p>
                    <span>
                        <span className='bold'>CRN:&nbsp;</span>
                        <span className='bold margin-right'>{offenderSummary.otherIds.crn}</span>
                    </span>
                    <br/>
                    <span id='provider'>
                        <span id='provider-label'>Provider:&nbsp;</span>
                        <span className='margin-right' aria-labelledby="provider-label">{provider(offenderSummary)}</span>
                    </span>
                    <br/>
                    <span id='officer'>
                        <span id='officer-label'>Officer name:&nbsp;</span>
                        <span className='margin-right' aria-labelledby="officer-label">{officer(offenderSummary)}</span>
                    </span>
                </p>
                <p><AddContactLink offenderId={offenderSummary.offenderId} rankIndex={offenderSummary.rankIndex}/></p>
            </div>
        </div>
    </li>
)

const provider = offenderSummary => {
    const activeManager = activeOffenderManager(offenderSummary)
    if (activeManager && activeManager.probationArea) {
        return activeManager.probationArea.description
    }
}

const activeOffenderManager = offenderSummary => {
    if (offenderSummary.offenderManagers) {
        return offenderSummary
            .offenderManagers
            .filter(managers => managers.active === true)
            .reduce((accumulator, currentValue) => accumulator || currentValue, null)
    }
}

const officer = offenderSummary => {
    const activeManager = activeOffenderManager(offenderSummary)
    if (activeManager && activeManager.staff) {
        return activeManager.staff.forenames + ' ' + activeManager.staff.surname
    }
}

RestrictedOffenderSearchSummary.propTypes = {
    showOffenderDetails: PropTypes.func.isRequired,
    offenderSummary: PropTypes.shape({
        rankIndex: PropTypes.number.isRequired,
        offenderId: PropTypes.number.isRequired,
        otherIds: PropTypes.shape({
            crn: PropTypes.string.isRequired
        }).isRequired
    }).isRequired
};

export default RestrictedOffenderSearchSummary