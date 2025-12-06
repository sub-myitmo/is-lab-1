import React from 'react';
import '../styles/PersonView.css';

const ImportView = ({ userImport, onClose }) => {
    if (!userImport) return null;

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
                                <span>{userImport.id}</span>
                            </div>
                        </div>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <label>Status:</label>
                                <span>{userImport.status}</span>
                            </div>
                            <div className="detail-item">
                                <label>Count:</label>
                                <span>{userImport.count}</span>
                            </div>
                        </div>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <label>Errors:</label>
                                <span>{userImport.errors}</span>
                            </div>
                            <div className="detail-item">
                                <label>Creation date:</label>
                                <span>{userImport.creationDate}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal-actions">
                    <button onClick={onClose}>Close</button>
                </div>
            </div>
        </div>
    );
};

export default ImportView;