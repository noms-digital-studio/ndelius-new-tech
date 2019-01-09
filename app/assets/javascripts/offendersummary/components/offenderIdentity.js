import React from 'react';
import * as PropTypes from 'prop-types';
import moment from 'moment';

const OffenderIdentity = ({ offenderDetails }) => {
    return(
        <div className="govuk-grid-row govuk-!-margin-top-3 app-offender-header">
            <div className="govuk-grid-column-one-half">
                {offenderDetails.oneTimeNomisRef && <img alt={`Image of ${offenderDetails.firstName} ${offenderDetails.surname}`} className="offender-image" src={`offender/oneTimeNomisRef/${encodeURIComponent(offenderDetails.oneTimeNomisRef)}/image`}/>}
                {!offenderDetails.oneTimeNomisRef && <img alt="No offender image available" className="offender-image" src='assets/images/NoPhoto@2x.png'/>}
            </div>
            <div className="govuk-grid-column-one-half">
                { /* <div className="moj-risk-alert moj-risk-alert--medium" role="alert">medium risk of serious harm</div> */ }
            </div>
            <div className="app-offender-name">
                <h1 className="qa-offender-name govuk-heading-l govuk-!-margin-0 govuk-!-padding-0">{offenderDetails.surname}, {offenderDetails.firstName}</h1>
                <p className="govuk-body govuk-!-margin-0 govuk-!-margin-top-2">Date of birth</p>
                <p className="qa-offender-date-of-birth govuk-heading-m govuk-!-margin-0 govuk-!-padding-0">{moment(offenderDetails.dateOfBirth, 'YYYY-MM-DD').format('DD/MM/YYYY')}</p>
                <p className="govuk-body govuk-!-margin-0 govuk-!-margin-top-2">CRN</p>
                <p className="qa-offender-crn govuk-heading-m govuk-!-margin-0 govuk-!-padding-0">{offenderDetails.otherIds.crn}</p>
            </div>
        </div>
    );
};

OffenderIdentity.propTypes = {
    offenderDetails: PropTypes.shape({
        firstName: PropTypes.string.isRequired,
        surname: PropTypes.string.isRequired,
        dateOfBirth: PropTypes.string.isRequired,
        otherIds : PropTypes.shape({
            crn: PropTypes.string.isRequired
        }),
        oneTimeNomisRef: PropTypes.string
    }).isRequired
};

export default OffenderIdentity;