import React from 'react';
import { formatDate, getDisplayName } from '../../utils/helpers';
import '../styles/PersonView.css';

const PersonView = ({ person, onClose, onEdit }) => {
    if (!person) return null;

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
                                <span>{person.id}</span>
                            </div>
                            <div className="detail-item">
                                <label>Name:</label>
                                <span>{person.name}</span>
                            </div>
                            <div className="detail-item">
                                <label>Passport ID:</label>
                                <span>{person.passportID}</span>
                            </div>
                            <div className="detail-item">
                                <label>Creation Date:</label>
                                <span>{formatDate(person.creationDate)}</span>
                            </div>
                        </div>
                    </div>

                    <div className="detail-section">
                        <h3>Physical Characteristics</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <label>Height:</label>
                                <span>{person.height} cm</span>
                            </div>
                            <div className="detail-item">
                                <label>Weight:</label>
                                <span>{person.weight} kg</span>
                            </div>
                            <div className="detail-item">
                                <label>Eye Color:</label>
                                <span>{getDisplayName(person.eyeColor)}</span>
                            </div>
                            <div className="detail-item">
                                <label>Hair Color:</label>
                                <span>{getDisplayName(person.hairColor)}</span>
                            </div>
                        </div>
                    </div>

                    <div className="detail-section">
                        <h3>Location Information</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <label>Nationality:</label>
                                <span>{getDisplayName(person.nationality)}</span>
                            </div>
                            {person.location && (
                                <>
                                    <div className="detail-item">
                                        <label>Location X:</label>
                                        <span>{person.location.x}</span>
                                    </div>
                                    <div className="detail-item">
                                        <label>Location Y:</label>
                                        <span>{person.location.y}</span>
                                    </div>
                                    <div className="detail-item">
                                        <label>Location Z:</label>
                                        <span>{person.location.z}</span>
                                    </div>
                                </>
                            )}
                            {person.coordinates && (
                                <>
                                    <div className="detail-item">
                                        <label>Coordinates X:</label>
                                        <span>{person.coordinates.x}</span>
                                    </div>
                                    <div className="detail-item">
                                        <label>Coordinates Y:</label>
                                        <span>{person.coordinates.y}</span>
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </div>

                <div className="modal-actions">
                    <button onClick={onClose}>Close</button>
                    <button onClick={() => onEdit(person)} className="btn-edit">
                        Edit Person
                    </button>
                </div>
            </div>
        </div>
    );
};

export default PersonView;