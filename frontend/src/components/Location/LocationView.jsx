import React from 'react';
import '../styles/PersonView.css';

const LocationView = ({ location, onClose, onEdit }) => {
    if (!location) return null;

    return (
        <div className="person-view-modal">
            <div className="modal-content">
                <div className="modal-header">
                    <h2>Person Details</h2>
                    <button onClick={onClose} className="close-button">&times;</button>
                </div>
                <div className="person-details">
                    <div className="detail-section">
                        <h3>Basic Information</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <label>ID:</label>
                                <span>{location.id}</span>
                            </div>
                            <div className="detail-item">
                                <label>X:</label>
                                <span>{location.x}</span>
                            </div>
                            <div className="detail-item">
                                <label>Y:</label>
                                <span>{location.y}</span>
                            </div>
                            <div className="detail-item">
                                <label>Z:</label>
                                <span>{location.z}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal-actions">
                    <button onClick={onClose}>Close</button>
                    <button onClick={() => onEdit(location)} className="btn-edit">
                        Edit Location
                    </button>
                </div>
            </div>
        </div>
    );
};

export default LocationView;