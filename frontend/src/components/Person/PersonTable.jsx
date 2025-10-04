import React, { useState, useMemo } from 'react';
import { formatDate, getDisplayName } from '../../utils/helpers';
import {FIELDS} from '../../utils/constants';
import '../styles/PersonTable.css';

const PersonTable = ({
                         persons,
                         onEdit,
                         onDelete,
                         onView,
                         onSort,
                         sortField,
                         sortDirection
                     }) => {
    const [selectedPerson, setSelectedPerson] = useState(null);

    const handleSort = (field) => {
        if (onSort) {
            onSort(field);
        }
    };

    const getSortIndicator = (field) => {
        if (sortField !== field) return '↕';
        return sortDirection === 'asc' ? '↑' : '↓';
    };

    if (!persons || persons.length === 0) {
        return (
            <div className="no-data">
                <p>No persons found</p>
            </div>
        );
    }

    return (
        <div className="person-table-container">
            <table className="person-table">
                <thead>
                <tr>
                    {FIELDS.map(column => (
                        <th key={column.key}>
                            {column.sortable ? (
                                <button
                                    onClick={() => handleSort(column.key)}
                                    className="sort-button"
                                >
                                    {column.label} {getSortIndicator(column.key)}
                                </button>
                            ) : (
                                column.label
                            )}
                        </th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {persons.map(person => (
                    <tr key={person.id} className={selectedPerson === person.id ? 'selected' : ''}>
                        <td>{person.id}</td>
                        <td>{person.name}</td>
                        <td>{person.passportID}</td>
                        <td>{getDisplayName(person.nationality)}</td>
                        <td>{getDisplayName(person.eyeColor)}</td>
                        <td>{getDisplayName(person.hairColor)}</td>
                        <td>{person.height} cm</td>
                        <td>{person.weight} kg</td>
                        <td>{formatDate(person.creationDate)}</td>
                        <td>({person.location.x}, {person.location.y}, {person.location.z})</td>
                        <td>({person.coordinates.x}, {person.coordinates.y})</td>
                        <td className="actions">
                            <button
                                onClick={() => onView(person)}
                                className="btn-view"
                                title="View details"
                            >
                                👁️
                            </button>
                            <button
                                onClick={() => onEdit(person)}
                                className="btn-edit"
                                title="Edit"
                            >
                                ✏️
                            </button>
                            <button
                                onClick={() => onDelete(person)}
                                className="btn-delete"
                                title="Delete"
                            >
                                🗑️
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default PersonTable;