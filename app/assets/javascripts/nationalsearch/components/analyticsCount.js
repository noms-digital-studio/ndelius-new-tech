import PropTypes from 'prop-types'

const AnalyticsCount = ({description, count, fetching}) => (
    <div>
        <p>{description}</p>
        {fetching && <p>?</p>}
        {!fetching && <p>{count}</p>}
    </div>
);


AnalyticsCount.propTypes = {
    count: PropTypes.number.isRequired,
    description: PropTypes.string.isRequired,
    fetching: PropTypes.bool.isRequired
};


export default AnalyticsCount;
