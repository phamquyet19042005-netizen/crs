// path: crs-frontend/src/api/axiosClient.ts
// purpose: axios instance duy nhat cua toan bo frontend, TRO DUY NHAT ve api-gateway,
// khong goi thang bat ky service nao khac

import axios from 'axios';

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default axiosClient;
