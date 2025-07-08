import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token'), // 存储token
    user: JSON.parse(localStorage.getItem('user')),
  }),
  getters: {
    // 使用getter判断是否认证
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    loginSuccess(loginData) {
      // 登录成功后，我们现在存的是token和从token解析出的用户信息
      this.token = loginData.token;
      // 简单地从token中解码用户信息 (注意：这不安全，仅用于显示)
      try {
        const payload = JSON.parse(atob(loginData.token.split('.')[1]));
        this.user = { username: payload.sub, roles: payload.roles };
        localStorage.setItem('user', JSON.stringify(this.user));
      } catch (e) {
        console.error("Could not parse token", e);
        this.user = null;
      }
      localStorage.setItem('token', this.token);
    },
    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },
  },
});