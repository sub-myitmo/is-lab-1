import React, {useState, useEffect} from 'react';
import {Color, Country} from '../../utils/constants';
import {validateCoordinates} from '../../utils/helpers';
import personService from '../../services/personService';
import '../styles/PersonForm.css';

const CoordinatesForm = ({coordinates, onSave, onCancel, isEditing = false}) => {
    const [formData, setFormData] = useState({
        x: '',
        y: ''
    });

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (coordinates) {
            setFormData({
                x: coordinates.x || '',
                y: coordinates.y || ''
            });
        }
    }, [coordinates]);

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

        const validationErrors = validateCoordinates(validationData);

        console.log('Validation errors:', validationErrors); // Для отладки

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setLoading(true);
        try {
            const coordinatesData = {
                x: parseFloat(formData.x),
                y: parseInt(formData.y)
            };


            console.log('Sending data:', coordinatesData); // Для отладки

            let result;
            if (isEditing) {
                result = await personService.updateCoordinates(coordinates.id, coordinatesData);
            } else {
                result = await personService.createCoordinates(coordinatesData);
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
            <h2>{isEditing ? 'Edit Coordinates' : 'Create New Coordinates'}</h2>

            <form onSubmit={handleSubmit} className="person-form">
                <div className="form-section">
                    <h3>Basic Information</h3>

                    <div className="form-row">
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

export default CoordinatesForm;