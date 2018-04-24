import PropTypes from "prop-types";
import {Component} from 'react'

class AreaFilter extends Component {
    constructor(props) {
        super(props);
        this.state = {
            expanded: true
        }
    }
    componentWillReceiveProps(nextProps) {
        const {search, searchTerm} = this.props

        // TODO use ImmutableJS instead
        if (JSON.stringify(this.props.probationAreasFilter) !== JSON.stringify(nextProps.probationAreasFilter)) {
            search(searchTerm, nextProps.probationAreasFilter)
        }
    }
    toggleExpanded() {
        this.setState({expanded: !this.state.expanded})
    }
    render() {
        const {byProbationArea, probationAreasFilter, addAreaFilter, removeAreaFilter} = this.props
        const {expanded} = this.state
        const toggleFilter = (code, description) => {
            if (probationAreasFilter.indexOf(code) > -1) {
                removeAreaFilter(code)
            } else {
                addAreaFilter(code, description)
            }
        }
        return (
            <div className='js-stick-at-top-when-scrolling'>
                {shouldDisplayFilter(byProbationArea) &&
                <div  className='filter'>
                    <button onClick={() => this.toggleExpanded()} type="button" aria-expanded={expanded} aria-controls="filters-providers" className={expanded ? 'open' : 'closed'}>
                        <div id='provider-select-label' className='bold-small'>
                            Provider
                        </div>
                        <div id='selected' className='font-xsmall'>{probationAreasFilter.length} selected</div>
                    </button>
                    <div role='group'aria-labelledby='provider-select-label' id='filter-providers' className={expanded ? 'open filter-container' : 'closed filter-container'}>
                        <div>
                        {byProbationArea.map(probationArea => (
                            <label key={probationArea.code} htmlFor={probationArea.code} className='font-xsmall'>
                                <input className='filter-option' tabIndex={1} type='checkbox' value={probationArea.code} id={probationArea.code}
                                       checked={probationAreasFilter.indexOf(probationArea.code) > -1}
                                       onChange={() => toggleFilter(probationArea.code, probationArea.description)}
                                       aria-controls='live-offender-results'
                                />
                                <span>{probationArea.description} ({probationArea.count})</span>
                            </label>
                        ))}
                        </div>
                    </div>
                </div>
                }
            </div>
        )
    }
}

AreaFilter.propTypes = {
    searchTerm: PropTypes.string.isRequired,
    byProbationArea: PropTypes.arrayOf(PropTypes.shape({
        code: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        count: PropTypes.number.isRequired
    })).isRequired,
    probationAreasFilter: PropTypes.arrayOf(PropTypes.string).isRequired,
    addAreaFilter: PropTypes.func.isRequired,
    removeAreaFilter: PropTypes.func.isRequired,
    search: PropTypes.func.isRequired
};

const shouldDisplayFilter = byProbationArea => byProbationArea.length > 0
export default AreaFilter;