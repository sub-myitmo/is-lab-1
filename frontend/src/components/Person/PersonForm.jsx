import React, {useState, useEffect} from 'react';
import {Color, Country} from '../../utils/constants';
import {
    validatePerson,
    validateLocation,
    validateCoordinates,
    parseCoordinatesData,
    parseLocationData
} from '../../utils/helpers';
import personService from '../../services/personService';
import '../styles/PersonForm.css';

const PersonForm = ({person, onSave, onCancel, isEditing = false}) => {
    const [formData, setFormData] = useState({
        name: '',
        height: '',
        weight: '',
        passportID: '',
        eyeColor: '',
        hairColor: '',
        nationality: '',
        coordinates: {x: '', y: ''},
        location: {x: '', y: '', z: ''}
    });

    const [errors, setErrors] = useState({});
    const [locations, setLocations] = useState([]);
    const [coordinates, setCoordinates] = useState([]);
    // const [useExistingLocation, setUseExistingLocation] = useState(false);
    // const [useExistingCoordinates, setUseExistingCoordinates] = useState(false);
    const [selectedLocationId, setSelectedLocationId] = useState('');
    const [selectedCoordinatesId, setSelectedCoordinatesId] = useState('');
    const [loading, setLoading] = useState(false);

    // Состояния для модальных окон
    const [showLocationModal, setShowLocationModal] = useState(false);
    const [showCoordinatesModal, setShowCoordinatesModal] = useState(false);
    const [newLocation, setNewLocation] = useState({x: '', y: '', z: ''});
    const [newCoordinates, setNewCoordinates] = useState({x: '', y: ''});
    const [locationErrors, setLocationErrors] = useState({});
    const [coordinatesErrors, setCoordinatesErrors] = useState({});

    useEffect(() => {
        if (person) {
            setFormData({
                name: person.name || '',
                height: person.height || '',
                weight: person.weight || '',
                passportID: person.passportID || '',
                eyeColor: person.eyeColor || '',
                hairColor: person.hairColor || '',
                nationality: person.nationality || '',
                coordinates: person.coordinates || {x: '', y: ''},
                location: person.location || {x: '', y: '', z: ''}
            });
            setSelectedLocationId(person.location.id);
            setSelectedCoordinatesId(person.coordinates.id);
        }
        loadExistingData();
    }, [person]);

    const loadExistingData = async () => {
        try {
            const [locResponse, coordResponse] = await Promise.all([
                personService.getLocations(),
                personService.getCoordinates()
            ]);
            setLocations(locResponse.data);
            setCoordinates(coordResponse.data);
        } catch (error) {
            console.error('Error loading existing data:', error);
        }
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        if (errors[name]) {
            setErrors(prev => ({...prev, [name]: ''}));
        }

        // console.log(formData);
    };

    const handleNewLocationChange = (field, value) => {
        setNewLocation(prev => ({
            ...prev,
            [field]: value
        }));

        if (locationErrors[field]) {
            setLocationErrors(prev => ({...prev, [field]: ''}));
        }
    };

    const handleNewCoordinatesChange = (field, value) => {
        setNewCoordinates(prev => ({
            ...prev,
            [field]: value
        }));

        if (coordinatesErrors[field]) {
            setCoordinatesErrors(prev => ({...prev, [field]: ''}));
        }
    };

    const handleCreateLocation = async () => {
        const validationErrors = validateLocation(newLocation);
        if (Object.keys(validationErrors).length > 0) {
            setLocationErrors(validationErrors);
            return;
        }

        try {
            const locationData = {
                ...newLocation,
                x: parseFloat(newLocation.x),
                y: parseFloat(newLocation.y),
                z: parseFloat(newLocation.z)
            };

            const result = await personService.createLocation(locationData);
            await loadExistingData(); // Обновляем список локаций
            setSelectedLocationId(result.data.id);
            // setUseExistingLocation(true);
            setShowLocationModal(false);
            setNewLocation({x: '', y: '', z: ''});
            setLocationErrors({});

            alert('Location created successfully!');
        } catch (error) {
            console.error('Error creating location:', error);
            alert(`Error creating location: ${error.response?.data?.error || error.message}`);
        }
    };

    const handleCreateCoordinates = async () => {
        const validationErrors = validateCoordinates(newCoordinates);
        if (Object.keys(validationErrors).length > 0) {
            setCoordinatesErrors(validationErrors);
            return;
        }

        try {
            const coordinatesData = {
                ...newCoordinates,
                x: parseFloat(newCoordinates.x),
                y: parseFloat(newCoordinates.y)
            };

            const result = await personService.createCoordinates(coordinatesData);
            await loadExistingData(); // Обновляем список координат
            setSelectedCoordinatesId(result.data.id);
            // setUseExistingCoordinates(true);
            setShowCoordinatesModal(false);
            setNewCoordinates({x: '', y: ''});
            setCoordinatesErrors({});

            alert('Coordinates created successfully!');
        } catch (error) {
            console.error('Error creating coordinates:', error);
            alert(`Error creating coordinates: ${error.response?.data?.error || error.message}`);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        console.log('Submit clicked'); // Для отладки
        console.log(formData);

        // Создаем объект для валидации с учетом выбранных опций
        const validationData = {
            ...formData,
            locationId: selectedLocationId,
            coordinatesId: selectedCoordinatesId
        };

        const validationErrors = validatePerson(validationData);

        console.log('Validation errors:', validationErrors); // Для отладки

        // проверка уникальности passportID
        let checkResult = await personService.checkPassportID(validationData.passportID);
        if (checkResult.data.count !== 0 && (person === null || (checkResult.data.count !== person.id))) validationErrors.passportID = "Person with this passportID already exists";
        console.log('Response:', checkResult);

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setLoading(true);
        try {
            // Подготавливаем данные для отправки
            const personData = {
                name: formData.name.trim(),
                height: parseInt(formData.height),
                weight: parseInt(formData.weight),
                passportID: formData.passportID.trim(),
                eyeColor: formData.eyeColor,
                hairColor: formData.hairColor || null,
                nationality: formData.nationality,
            };

            // Добавляем координаты и локацию только если не используем существующие
            // if (!useExistingCoordinates) {
            //     personData.coordinates = parseCoordinatesData(formData.coordinates);
            // }

            // if (!useExistingLocation) {
            //     personData.location = parseLocationData(formData.location);
            // }

            console.log('Sending data:', personData); // Для отладки
            console.log('Location ID:', selectedLocationId);
            console.log('Coordinates ID:', selectedCoordinatesId);

            let result;
            if (isEditing) {
                result = await personService.update(person.id, personData, selectedLocationId, selectedCoordinatesId);
            } else {
                result = await personService.create(
                    personData,
                    selectedLocationId,
                    selectedCoordinatesId
                );
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
            <h2>{isEditing ? 'Edit Person' : 'Create New Person'}</h2>

            <form onSubmit={handleSubmit} className="person-form">
                {/* Basic Information Section (остается без изменений) */}
                <div className="form-section">
                    <h3>Basic Information</h3>

                    <div className="form-group">
                        <label>Name *</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className={errors.name ? 'error' : ''}
                        />
                        {errors.name && <span className="error-text">{errors.name}</span>}
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Height *</label>
                            <input
                                type="number"
                                name="height"
                                value={formData.height}
                                onChange={handleChange}
                                className={errors.height ? 'error' : ''}
                            />
                            {errors.height && <span className="error-text">{errors.height}</span>}
                        </div>

                        <div className="form-group">
                            <label>Weight *</label>
                            <input
                                type="number"
                                name="weight"
                                value={formData.weight}
                                onChange={handleChange}
                                className={errors.weight ? 'error' : ''}
                            />
                            {errors.weight && <span className="error-text">{errors.weight}</span>}
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Passport ID *</label>
                        <input
                            type="text"
                            name="passportID"
                            value={formData.passportID}
                            onChange={handleChange}
                            className={errors.passportID ? 'error' : ''}
                        />
                        {errors.passportID && <span className="error-text">{errors.passportID}</span>}
                    </div>
                </div>

                {/* Appearance Section (остается без изменений) */}
                <div className="form-section">
                    <h3>Appearance</h3>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Eye Color *</label>
                            <select
                                name="eyeColor"
                                value={formData.eyeColor.toUpperCase()}
                                onChange={handleChange}
                                className={errors.eyeColor ? 'error' : ''}
                            >
                                <option value="">Select Eye Color</option>
                                {Object.values(Color).map(color => (
                                    <option key={color} value={color}>
                                        {color.charAt(0) + color.slice(1).toLowerCase()}
                                    </option>
                                ))}
                            </select>
                            {errors.eyeColor && <span className="error-text">{errors.eyeColor}</span>}
                        </div>

                        <div className="form-group">
                            <label>Hair Color</label>
                            <select
                                name="hairColor"
                                value={formData.hairColor.toUpperCase()}
                                onChange={handleChange}
                            >
                                <option value="">Select Hair Color</option>
                                {Object.values(Color).map(color => (
                                    <option key={color} value={color}>
                                        {color.charAt(0) + color.slice(1).toLowerCase()}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Nationality *</label>
                        <select
                            name="nationality"
                            value={formData.nationality.toUpperCase()}
                            onChange={handleChange}
                            className={errors.nationality ? 'error' : ''}
                        >
                            <option value="">Select Nationality</option>
                            {Object.values(Country).map(country => (
                                <option key={country} value={country}>
                                    {country.charAt(0) + country.slice(1).toLowerCase()}
                                </option>
                            ))}
                        </select>
                        {errors.nationality && <span className="error-text">{errors.nationality}</span>}
                    </div>
                </div>

                {/* Coordinates Section */}
                <div className="form-section">
                    <h3>Coordinates</h3>

                    {/* После селектора Coordinates */}
                    {errors.coordinatesId && (
                        <span className="error-text">{errors.coordinatesId}</span>
                    )}

                    <div className="form-row">
                        <div className="form-group">
                            <label>Select Coordinates</label>
                            <select
                                value={selectedCoordinatesId}
                                onChange={(e) => setSelectedCoordinatesId(e.target.value)}
                            >
                                <option value="">Select coordinates</option>
                                {coordinates.map(coord => (
                                    <option key={coord.id} value={coord.id}>
                                        {`X: ${coord.x}, Y: ${coord.y}`}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <button
                                type="button"
                                onClick={() => setShowCoordinatesModal(true)}
                                className="btn-secondary"
                            >
                                Create New Coordinates
                            </button>
                        </div>
                    </div>
                </div>


                {/* Location Section */}
                <div className="form-section">
                    <h3>Location</h3>

                    {/* После селектора Location */}
                    {errors.locationId && (
                        <span className="error-text">{errors.locationId}</span>
                    )}

                    <div className="form-row">
                        <div className="form-group">
                            <label>Select Location</label>
                            <select
                                value={selectedLocationId}
                                onChange={(e) => setSelectedLocationId(e.target.value)}
                            >
                                <option value="">Select location</option>
                                {locations.map(loc => (
                                    <option key={loc.id} value={loc.id}>
                                        {`X: ${loc.x}, Y: ${loc.y}, Z: ${loc.z}`}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <button
                                type="button"
                                onClick={() => setShowLocationModal(true)}
                                className="btn-secondary"
                            >
                                Create New Location
                            </button>
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

            {/* Location Creation Modal */}
            {showLocationModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <div className="modal-header">
                            <h3>Create New Location</h3>
                            <button onClick={() => setShowLocationModal(false)}>×</button>
                        </div>
                        <div className="modal-content">
                            <div className="form-row">
                                <div className="form-group">
                                    <label>Location X *</label>
                                    <input
                                        type="number"
                                        step="any"
                                        value={newLocation.x}
                                        onChange={(e) => handleNewLocationChange('x', e.target.value)}
                                        className={locationErrors.x ? 'error' : ''}
                                    />
                                    {locationErrors.x && <span className="error-text">{locationErrors.x}</span>}
                                </div>
                                <div className="form-group">
                                    <label>Location Y *</label>
                                    <input
                                        type="number"
                                        step="any"
                                        value={newLocation.y}
                                        onChange={(e) => handleNewLocationChange('y', e.target.value)}
                                        className={locationErrors.y ? 'error' : ''}
                                    />
                                    {locationErrors.y && <span className="error-text">{locationErrors.y}</span>}
                                </div>
                                <div className="form-group">
                                    <label>Location Z</label>
                                    <input
                                        type="number"
                                        step="any"
                                        value={newLocation.z}
                                        onChange={(e) => handleNewLocationChange('z', e.target.value)}
                                    />
                                </div>
                            </div>
                        </div>
                        <div className="modal-actions">
                            <button onClick={() => setShowLocationModal(false)}>Cancel</button>
                            <button onClick={handleCreateLocation}>Create Location</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Coordinates Creation Modal */}
            {showCoordinatesModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <div className="modal-header">
                            <h3>Create New Coordinates</h3>
                            <button onClick={() => setShowCoordinatesModal(false)}>×</button>
                        </div>
                        <div className="modal-content">
                            <div className="form-row">
                                <div className="form-group">
                                    <label>Coordinates X *</label>
                                    <input
                                        type="number"
                                        step="any"
                                        value={newCoordinates.x}
                                        onChange={(e) => handleNewCoordinatesChange('x', e.target.value)}
                                        className={coordinatesErrors.x ? 'error' : ''}
                                    />
                                    {coordinatesErrors.x && <span className="error-text">{coordinatesErrors.x}</span>}
                                </div>
                                <div className="form-group">
                                    <label>Coordinates Y *</label>
                                    <input
                                        type="number"
                                        step="any"
                                        value={newCoordinates.y}
                                        onChange={(e) => handleNewCoordinatesChange('y', e.target.value)}
                                        className={coordinatesErrors.y ? 'error' : ''}
                                    />
                                    {coordinatesErrors.y && <span className="error-text">{coordinatesErrors.y}</span>}
                                </div>
                            </div>
                        </div>
                        <div className="modal-actions">
                            <button onClick={() => setShowCoordinatesModal(false)}>Cancel</button>
                            <button onClick={handleCreateCoordinates}>Create Coordinates</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PersonForm;