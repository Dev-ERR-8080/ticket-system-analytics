import api from './api';

export const userService = {
  getAllUsers: async () => {
    const response = await api.get('/users');
    return response.data;
  },

  createUser: async (name, role) => {
    const response = await api.post('/users', { name, role });
    return response.data;
  },
};
