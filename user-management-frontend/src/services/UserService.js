import axios from 'axios';
import { useAuthStore } from '@/store/auth2'; // 1. 导入auth store


// 创建一个Axios实例，并进行基础配置
const apiClient = axios.create({
  // 重要：这里是你的后端API的基础URL
  // 请确保端口号与你SpringBoot应用的端口号一致
  baseURL: process.env.VUE_APP_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 2. 使用Axios的请求拦截器
apiClient.interceptors.request.use(config => {
  const authStore = useAuthStore();
  
  if (authStore.isAuthenticated) {
    // 如果用户已登录，则为每个请求添加Authorization头
    // config.headers.Authorization = authStore.basicAuthHeader;
    config.headers.Authorization = `Bearer ${authStore.token}`;
  }
  
  return config;
}, error => {
  return Promise.reject(error);
});

// 定义用户相关的API请求
export default {
  /**
   * 获取用户列表（支持分页和查询）
   * @param {number} pageNum - 页码
   * @param {number} pageSize - 每页大小
   * @param {string} keyword - 搜索关键字
   */
  getUsers(pageNum = 1, pageSize = 5, keyword = '') {
    // 我们需要为 admin/123456 生成Basic Auth的Header
    // const credentials = btoa('admin:123456'); // Base64编码

    return apiClient.get('/users', {
      params: {
        pageNum,
        pageSize,
        keyword
      }
      // headers: {
      //   // 在请求头中添加认证信息
      //   'Authorization': `Basic ${credentials}`
      // }
    });
  },

  /**
   * 根据ID删除用户
   * @param {number} id - 要删除的用户ID
   */
  deleteUser(id) {
    return apiClient.delete(`/users/${id}`);
  },

  // deleteUser(id) {
  //   const credentials = btoa('admin:123456');
  //   // 发送DELETE请求到 /users/{id}
  //   return apiClient.delete(`/users/${id}`, {
  //     headers: {
  //       'Authorization': `Basic ${credentials}`
  //     }
  //   });
  // },


   /**
   * 创建一个新用户
   * @param {object} userData - 包含新用户信息的数据对象
   */
    createUser(userData) {
      return apiClient.post('/users', userData);
    },

  // createUser(userData) {
  //       const credentials = btoa('admin:123456');
  //       // 发送POST请求，将userData作为请求体
  //       return apiClient.post('/users', userData, {
  //       headers: {
  //           'Authorization': `Basic ${credentials}`
  //       }
  //       });
  //   },


     /**
   * 根据ID获取单个用户信息
   * @param {number} id - 用户ID
   */
  getUserById(id) {
    return apiClient.get(`/users/${id}`);
  },

  // getUserById(id) {
  //   const credentials = btoa('admin:123456');
  //   return apiClient.get(`/users/${id}`, {
  //     headers: {
  //       'Authorization': `Basic ${credentials}`
  //     }
  //   });
  // },




  /**
   * 更新一个已存在的用户
   * @param {number} id - 要更新的用户ID
   * @param {object} userData - 包含更新后信息的数据对象
   */
  updateUser(id, userData) {
    return apiClient.put(`/users/${id}`, userData);
  }



  // updateUser(id, userData) {
  //   const credentials = btoa('admin:123456');
  //   return apiClient.put(`/users/${id}`, userData, {
  //     headers: {
  //       'Authorization': `Basic ${credentials}`
  //     }
  //   });
  // }
  
  // 我们之后会在这里添加 getUser, createUser, updateUser, deleteUser 等方法
};