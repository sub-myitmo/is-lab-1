import React, { useState, useMemo } from 'react';
import {COORDINATES_FIELDS} from '../../utils/constants';
import '../styles/PersonTable.css';

const CoordinatesTable = ({
                         coordinatesArr,
                         onEdit,
                         onDelete,
                         onView,
                         onSort,
                         sortField,
                         sortDirection
                     }) => {
    const [selectedCoordinates, setSelectedCoordinates] = useState(null);

    const handleSort = (field) => {
        if (onSort) {
            onSort(field);
        }
    };

    const getSortIndicator = (field) => {
        if (sortField !== field) return '↕';
        return sortDirection === 'asc' ? '↑' : '↓';
    };

    if (!coordinatesArr || coordinatesArr.length === 0) {
        return (
            <div className="no-data">
                <p>No coordinates found</p>
            </div>
        );
    }

    return (
        <div className="person-table-container">
            <table className="person-table">
                <thead>
                <tr>
                    {COORDINATES_FIELDS.map(column => (
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
                {coordinatesArr.map(coordinates => (
                    <tr key={coordinates.id} className={selectedCoordinates === coordinates.id ? 'selected' : ''}>
                        <td>{coordinates.id}</td>
                        <td>{coordinates.x}</td>
                        <td>{coordinates.y}</td>
                        <td className="actions">
                            <button
                                onClick={() => onView(coordinates)}
                                className="btn-view"
                                title="View details"
                            >
                                👁️
                            </button>
                            <button
                                onClick={() => onEdit(coordinates)}
                                className="btn-edit"
                                title="Edit"
                            >
                                ✏️
                            </button>
                            <button
                                onClick={() => onDelete(coordinates)}
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

export default CoordinatesTable;