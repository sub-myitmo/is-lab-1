import React from 'react';
import '../styles/PersonView.css';

const CoordinatesView = ({ coordinates, onClose, onEdit }) => {
    if (!coordinates) return null;

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
                                <span>{coordinates.id}</span>
                            </div>
                            <div className="detail-item">
                                <label>X:</label>
                                <span>{coordinates.x}</span>
                            </div>
                            <div className="detail-item">
                                <label>Y:</label>
                                <span>{coordinates.y}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal-actions">
                    <button onClick={onClose}>Close</button>
                    <button onClick={() => onEdit(coordinates)} className="btn-edit">
                        Edit Coordinates
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CoordinatesView;