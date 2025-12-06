import React, {useState, useEffect} from 'react';
import {Color, Country} from '../../utils/constants';
import {validateLocation} from '../../utils/helpers';
import personService from '../../services/mainService';
import '../styles/PersonForm.css';

const LocationForm = ({location, onSave, onCancel, isEditing = false}) => {
    const [formData, setFormData] = useState({
        x: '',
        y: '',
        z: ''
    });

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    // // Состояния для модальных окон
    // const [showLocationModal, setShowLocationModal] = useState(false);
    // const [showCoordinatesModal, setShowCoordinatesModal] = useState(false);
    // const [newLocation, setNewLocation] = useState({x: '', y: '', z: ''});
    // const [newCoordinates, setNewCoordinates] = useState({x: '', y: ''});
    // const [locationErrors, setLocationErrors] = useState({});
    // const [coordinatesErrors, setCoordinatesErrors] = useState({});

    useEffect(() => {
        if (location) {
            setFormData({
                x: location.x || '',
                y: location.y || '',
                z: location.z || '',
            });
        }
    }, [location]);

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        if (errors[name]) {
            setErrors(prev => ({...prev, [name]: ''}));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        console.log('Submit clicked'); // Для отладки
        console.log(formData);

        // Создаем объект для валидации с учетом выбранных опций
        const validationData = {
            ...formData
        };

        const validationErrors = validateLocation(validationData);

        console.log('Validation errors:', validationErrors); // Для отладки

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setLoading(true);
        try {
            // Подготавливаем данные для отправки
            const locationData = {
                x: parseInt(formData.x),
                y: parseInt(formData.y),
                z: parseInt(formData.z),
            };


            console.log('Sending data:', locationData); // Для отладки

            let result;
            if (isEditing) {
                result = await personService.updateLocation(location.id, locationData);
            } else {
                result = await personService.createLocation(locationData);
            }

            console.log('Response:', result); // Для отладки
            onSave(result.data);

        } catch (error) {
            console.error('Error saving person:', error);
            console.error('Error response:', error.response); // Для отладки
            alert(`Error: ${error.response?.data?.error || error.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="person-form-container">
            <h2>{isEditing ? 'Edit Location' : 'Create New Location'}</h2>

            <form onSubmit={handleSubmit} className="person-form">
                <div className="form-section">
                    <h3>Basic Information</h3>

                    <div className="form-group">
                        <label>X *</label>
                        <input
                            type="number"
                            name="x"
                            value={formData.x}
                            onChange={handleChange}
                            className={errors.x ? 'error' : ''}
                        />
                        {errors.x && <span className="error-text">{errors.x}</span>}
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Y *</label>
                            <input
                                type="number"
                                name="y"
                                value={formData.y}
                                onChange={handleChange}
                                className={errors.y ? 'error' : ''}
                            />
                            {errors.y && <span className="error-text">{errors.y}</span>}
                        </div>

                        <div className="form-group">
                            <label>Z *</label>
                            <input
                                type="number"
                                name="weight"
                                value={formData.z}
                                onChange={handleChange}
                                className={errors.z ? 'error' : ''}
                            />
                            {errors.z && <span className="error-text">{errors.z}</span>}
                        </div>
                    </div>
                </div>

                <div className="form-actions">
                    <button type="button" onClick={onCancel} disabled={loading}>
                        Cancel
                    </button>
                    <button type="submit" disabled={loading}>
                        {loading ? 'Saving...' : (isEditing ? 'Update' : 'Create')}
                    </button>
                    {(errors.length > 0) && <span className="error-text">Fix all problems!</span>}
                </div>
            </form>
        </div>
    );
};

export default LocationForm;