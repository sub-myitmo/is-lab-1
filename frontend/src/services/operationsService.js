import api from './api';

export const operationService = {
    getMinPassport: () => api.get('/operations/min-passport'),

    countNationalityLessThan: (nationality) =>
        api.get(`/operations/count/nationality-less-than/${nationality}`),

    countNationalityGreaterThan: (nationality) =>
        api.get(`/operations/count/nationality-greater-than/${nationality}`),

    countByHairColor: (hairColor) =>
        api.get(`/operations/count/hair-color/${hairColor}`),

    countByEyeColor: (eyeColor) =>
        api.get(`/operations/count/eye-color/${eyeColor}`),

};

export default operationService;