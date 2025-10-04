// import api from './api';
//
// export const authService = {
//     login: (username, password) =>
//         api.post('/auth/login', { username, password }),
//
//     register: (username, password, email) =>
//         api.post('/auth/register', { username, password, email }),
//
//     verifyToken: () => {
//         const token = localStorage.getItem('token');
//         if (!token) return Promise.reject('No token');
//
//         return api.get('/auth/verify', {
//             headers: { 'Authorization': `Bearer ${token}` }
//         });
//     },
//
//     logout: () => {
//         localStorage.removeItem('token');
//         localStorage.removeItem('username');
//     },
//
//     getToken: () => localStorage.getItem('token'),
//
//     isAuthenticated: () => {
//         const token = localStorage.getItem('token');
//         return !!token;
//     }
// };
//
// export default authService;