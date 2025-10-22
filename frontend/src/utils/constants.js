export const Color = {
    GREEN: 'GREEN',
    RED: 'RED',
    YELLOW: 'YELLOW',
    ORANGE: 'ORANGE',
    WHITE: 'WHITE'
};

export const Country = {
    RUSSIA: 'RUSSIA',
    SPAIN: 'SPAIN',
    INDIA: 'INDIA'
};

export const API_BASE_URL = 'http://127.0.0.1:20568/person-management/api';
export const WS_URL = 'ws://127.0.0.1:20568/person-management/websocket';

// export const API_BASE_URL = '/api';
// export const WS_URL = '/ws';


export const FIELDS = [
    { key: 'id', label: 'ID', sortable: true},
    { key: 'name', label: 'Name', sortable: true, filterable: true },
    { key: 'passportID', label: 'Passport ID', sortable: true, filterable: true },
    { key: 'nationality', label: 'Nationality', sortable: true, filterable: true },
    { key: 'eyeColor', label: 'Eye Color', sortable: true, filterable: true },
    { key: 'hairColor', label: 'Hair Color', sortable: true, filterable: true },
    { key: 'height', label: 'Height', sortable: true},
    { key: 'weight', label: 'Weight', sortable: true},
    { key: 'creationDate', label: 'Creation Date', sortable: true },
    { key: 'location', label: 'Location', sortable: false },
    { key: 'coordinates', label: 'Coordinates', sortable: false },
    { key: 'actions', label: 'Actions', sortable: false }
];
