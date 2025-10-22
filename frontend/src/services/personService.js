import api from './api';

export const personService = {
    // Basic CRUD operations
    getAll: (page = 0, size = 10, search = '', sortField='id', sortDirection='asc') =>
        api.get(`/persons?page=${page}&size=${size}&search=${search}&field=${sortField}&direction=${sortDirection}`),

    getById: (id) => api.get(`/persons/${id}`),

    create: (person, locationId = null, coordinatesId = null) => {
        const params = new URLSearchParams();
        if (locationId) params.append('locationId', locationId);
        if (coordinatesId) params.append('coordinatesId', coordinatesId);

        return api.post(`/persons/with-existing?${params.toString()}`, person);
    },

    update: (id, person, locationId = null, coordinatesId = null) => {
        const params = new URLSearchParams();
        if (locationId) params.append('locationId', locationId);
        if (coordinatesId) params.append('coordinatesId', coordinatesId);

        return api.put(`/persons/${id}?${params.toString()}`, person);
    },

    delete: (id) => api.delete(`/persons/${id}`),

    checkPassportID: (passportID) => api.get(`/persons/check/${passportID}`),


    // Location operations
    getLocations: () => api.get('/locations'),
    getLocationById: (id) => api.get(`/locations/${id}`),
    createLocation: (location) => api.post('/locations', location),
    // updateLocation: (id, location) => api.put(`/locations/${id}`, location),
    deleteLocation: (id) => api.delete(`/locations/${id}`),

    // Coordinates operations
    getCoordinates: () => api.get('/coordinates'),
    getCoordinatesById: (id) => api.get(`/coordinates/${id}`),
    createCoordinates: (coordinates) => api.post('/coordinates', coordinates),
    // updateCoordinates: (id, coordinates) => api.put(`/coordinates/${id}`, coordinates),
    deleteCoordinates: (id) => api.delete(`/coordinates/${id}`),
};

export default personService;