import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

// 使用 defineStore 创建一个store
// 第一个参数是store的唯一ID
export const useAuthStore = defineStore('auth', () => {
  // --- state (状态) ---
  // 使用 ref() 来定义响应式状态
  const user = ref(JSON.parse(localStorage.getItem('user')) || null);

  // --- getters (计算属性) ---
  // 使用 computed() 定义计算属性
  const isAuthenticated = computed(() => !!user.value);
  const username = computed(() => user.value?.username || 'Guest');
  const roles = computed(() => user.value?.roles || []);
  const basicAuthHeader = computed(() => {
    if (user.value) {
      return 'Basic ' + btoa(`${user.value.username}:${user.value.password}`);
    }
    return null;
  });

  // --- actions (方法) ---
  function loginSuccess(userData) {
    // 重要：在真实项目中，不应该将明文密码存入localStorage！
    // 这里为了简化Basic Auth的演示而存储。
    // 更好的方式是后端返回一个Token，我们只存储Token。
    user.value = userData;
    localStorage.setItem('user', JSON.stringify(userData));
  }

  function logout() {
    user.value = null;
    localStorage.removeItem('user');
  }

  // 返回state, getters, 和 actions
  return {
    user,
    isAuthenticated,
    username,
    roles,
    basicAuthHeader,
    loginSuccess,
    logout
  };
});