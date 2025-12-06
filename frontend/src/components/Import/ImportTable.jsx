import React, { useState, useMemo } from 'react';
import {IMPORT_FIELDS} from '../../utils/constants';
import '../styles/PersonTable.css';

const ImportTable = ({
                         userImports,
                         onView,
                         onSort,
                         sortField,
                         sortDirection
                     }) => {
    const [selectedUserImport, setSelectedUserImport] = useState(null);

    const handleSort = (field) => {
        if (onSort) {
            onSort(field);
        }
    };

    const getSortIndicator = (field) => {
        if (sortField !== field) return '↕';
        return sortDirection === 'asc' ? '↑' : '↓';
    };

    if (!userImports || userImports.length === 0) {
        return (
            <div className="no-data">
                <p>No imports found</p>
            </div>
        );
    }

    return (
        <div className="person-table-container">
            <table className="person-table">
                <thead>
                <tr>
                    {IMPORT_FIELDS.map(column => (
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
                {userImports.map(userImport => (
                    <tr key={userImport.id} className={selectedUserImport === userImport.id ? 'selected' : ''}>
                        <td>{userImport.id}</td>
                        <td>{userImport.status}</td>
                        <td>{userImport.count}</td>
                        <td>{userImport.errors}</td>
                        <td>{userImport.creationDate}</td>
                        <td className="actions">
                            <button
                                onClick={() => onView(userImport)}
                                className="btn-view"
                                title="View details"
                            >
                                👁️
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default ImportTable;