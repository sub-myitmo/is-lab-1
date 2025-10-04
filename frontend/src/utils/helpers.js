export const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString();
};

export const getDisplayName = (enumValue) => {
    if (!enumValue) return '-';
    return enumValue.charAt(0) + enumValue.slice(1).toLowerCase();
};

export const debounce = (func, wait) => {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
};

export const validatePerson = (person) => {
    const errors = {};

    if (!person.name || person.name.trim().length === 0) {
        errors.name = 'Name is required';
    } else if (person.name.length > 255) {
        errors.name = 'Name must be less than 255 characters';
    }

    if (!person.passportID || person.passportID.trim().length === 0) {
        errors.passportID = 'Passport ID is required';
    } else if (person.passportID.length > 255) {
        errors.passportID = 'Passport ID must be less than 255 characters';
    }

    if (!person.height || person.height <= 0) {
        errors.height = 'Height must be greater than 0';
    } else if (person.height > 300) {
        errors.height = 'Height must be less than 300';
    }

    if (!person.weight || person.weight <= 0) {
        errors.weight = 'Weight must be greater than 0';
    } else if (person.weight > 500) {
        errors.weight = 'Weight must be less than 500';
    }

    if (!person.eyeColor) {
        errors.eyeColor = 'Eye color is required';
    }

    if (!person.nationality) {
        errors.nationality = 'Nationality is required';
    }

    // Validate that if using existing, IDs are selected
    if (!person.locationId || person.locationId === '') {
        errors.locationId = 'Please select a location';
    }

    if (!person.coordinatesId || person.coordinatesId === '') {
        errors.coordinatesId = 'Please select coordinates';
    }

    return errors;
};

export const validateLocation = (location) => {
    const errors = {};

    if (!location.x && location.x !== 0) {
        errors.x = 'Location X is required';
    } else {
        const x = parseFloat(location.x);
        if (isNaN(x)) {
            errors.x = 'Location X must be a valid number';
        } else if (x < -1000 || x > 1000) {
            errors.x = 'Location X must be between -1000 and 1000';
        }
    }

    if (!location.y && location.y !== 0) {
        errors.y = 'Location Y is required';
    } else {
        const y = parseFloat(location.y);
        if (isNaN(y)) {
            errors.y = 'Location Y must be a valid number';
        } else if (y < -1000 || y > 1000) {
            errors.y = 'Location Y must be between -1000 and 1000';
        }
    }

    if (location.z !== undefined && location.z !== null && location.z !== '') {
        const z = parseFloat(location.z);
        if (isNaN(z)) {
            errors.z = 'Location Z must be a valid number';
        } else if (z < -1000 || z > 1000) {
            errors.z = 'Location Z must be between -1000 and 1000';
        }
    }

    return errors;
};

export const validateCoordinates = (coordinates) => {
    const errors = {};

    if (!coordinates.x && coordinates.x !== 0) {
        errors.x = 'Coordinates X is required';
    } else {
        const x = parseFloat(coordinates.x);
        if (isNaN(x)) {
            errors.x = 'Coordinates X must be a valid number';
        } else if (x < -1000 || x > 1000) {
            errors.x = 'Coordinates X must be between -1000 and 1000';
        }
    }

    if (!coordinates.y && coordinates.y !== 0) {
        errors.y = 'Coordinates Y is required';
    } else {
        const y = parseFloat(coordinates.y);
        if (isNaN(y)) {
            errors.y = 'Coordinates Y must be a valid number';
        } else if (y <= -657) {
            errors.y = 'Coordinates Y must be greater than -657';
        }
    }

    return errors;
};

// Вспомогательные функции для преобразования данных
export const parseLocationData = (location) => {
    if (!location) return null;

    return {
        x: location.x !== undefined && location.x !== '' ? parseFloat(location.x) : null,
        y: location.y !== undefined && location.y !== '' ? parseFloat(location.y) : null,
        z: location.z !== undefined && location.z !== '' ? parseFloat(location.z) : null
    };
};

export const parseCoordinatesData = (coordinates) => {
    if (!coordinates) return null;

    return {
        x: coordinates.x !== undefined && coordinates.x !== '' ? parseFloat(coordinates.x) : null,
        y: coordinates.y !== undefined && coordinates.y !== '' ? parseFloat(coordinates.y) : null
    };
};

// Функция для проверки, является ли значение числом
export const isNumeric = (value) => {
    if (value === undefined || value === null || value === '') return false;
    return !isNaN(parseFloat(value)) && isFinite(value);
};

// Функция для форматирования отображения Location и Coordinates
export const formatLocation = (location) => {
    if (!location) return 'No location';
    return `X: ${location.x}, Y: ${location.y}${location.z ? `, Z: ${location.z}` : ''}`;
};

export const formatCoordinates = (coordinates) => {
    if (!coordinates) return 'No coordinates';
    return `X: ${coordinates.x}, Y: ${coordinates.y}`;
};