import PropTypes from "prop-types";
import {Component} from 'react'

class AreaFilter extends Component {
    constructor(props) {
        super(props);
    }
    componentWillReceiveProps(nextProps) {
        const {search, searchTerm} = this.props
        if (this.props.areaFilter !== nextProps.areaFilter) {
            search(searchTerm, nextProps.areaFilter)
        }
    }
    render() {
        const {byProbationArea, areaFilter, addAreaFilter, removeAreaFilter} = this.props
        const toggleFilter = probationAreaCode => {
            if (areaFilter.indexOf(probationAreaCode) > -1) {
                removeAreaFilter(probationAreaCode)
            } else {
                addAreaFilter(probationAreaCode)
            }
        }
        return (
            <div className='js-stick-at-top-when-scrolling'>
                {shouldDisplayFilter(byProbationArea) &&
                <table className='filter'>
                    <thead>
                    <tr>
                        <th>
                            <span className='bold-medium'>Provider</span><br/>
                            <span className='font-small' id='selected'>{areaFilter.length} selected</span>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {byProbationArea.map(probationArea => (
                        <tr key={probationArea.code}>
                            <td className='multiple-choice margin-top'>
                                <input tabIndex={1} type='checkbox' value={probationArea.code} id={probationArea.code}
                                       checked={areaFilter.indexOf(probationArea.code) > -1}
                                       onChange={event => toggleFilter(event.target.value)}/>
                                <label
                                    htmlFor={probationArea.code}>{probationArea.description} ({probationArea.count})</label>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
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
    areaFilter: PropTypes.arrayOf(PropTypes.string).isRequired,
    addAreaFilter: PropTypes.func.isRequired,
    removeAreaFilter: PropTypes.func.isRequired,
    search: PropTypes.func.isRequired
};

const shouldDisplayFilter = byProbationArea => byProbationArea.length > 0
export default AreaFilter;