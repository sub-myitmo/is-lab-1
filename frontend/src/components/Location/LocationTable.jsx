import React, { useState, useMemo } from 'react';
import {LOCATION_FIELDS} from '../../utils/constants';
import '../styles/PersonTable.css';

const LocationTable = ({
                         locations,
                         onEdit,
                         onDelete,
                         onView,
                         onSort,
                         sortField,
                         sortDirection
                     }) => {
    const [selectedLocation, setSelectedLocation] = useState(null);

    const handleSort = (field) => {
        if (onSort) {
            onSort(field);
        }
    };

    const getSortIndicator = (field) => {
        if (sortField !== field) return '↕';
        return sortDirection === 'asc' ? '↑' : '↓';
    };

    if (!locations || locations.length === 0) {
        return (
            <div className="no-data">
                <p>No locations found</p>
            </div>
        );
    }

    return (
        <div className="person-table-container">
            <table className="person-table">
                <thead>
                <tr>
                    {LOCATION_FIELDS.map(column => (
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
                {locations.map(location => (
                    <tr key={location.id} className={selectedLocation === location.id ? 'selected' : ''}>
                        <td>{location.id}</td>
                        <td>{location.x}</td>
                        <td>{location.y}</td>
                        <td>{location.z}</td>
                        <td className="actions">
                            <button
                                onClick={() => onView(location)}
                                className="btn-view"
                                title="View details"
                            >
                                👁️
                            </button>
                            <button
                                onClick={() => onEdit(location)}
                                className="btn-edit"
                                title="Edit"
                            >
                                ✏️
                            </button>
                            <button
                                onClick={() => onDelete(location)}
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

export default LocationTable;