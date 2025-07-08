<template>
  <div class="login-container">
    <h1>系统登录</h1>
    <form @submit.prevent="handleLogin">
      <div v-if="error" class="error-message">{{ error }}</div>
      <div class="form-group">
        <label for="username">用户名:</label>
        <input type="text" id="username" v-model="username" required>
      </div>
      <div class="form-group">
        <label for="password">密码:</label>
        <input type="password" id="password" v-model="password" required>
      </div>
      <button type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

// 使用Vue 3 <script setup> 语法，更简洁
const router = useRouter();
const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const loading = ref(false);
const error = ref(null);

const handleLogin = async () => {
  loading.value = true;
  error.value = null;
  try {
    // 伪登录：我们不直接调用API，因为Basic Auth是每个请求都带的。
    // 我们只在本地设置登录状态。
    // 之后在API请求时，会从authStore中获取凭据。
    
    // 重要：在真实项目中，这里应该调用一个登录API，
    // 后端验证成功后返回用户信息和Token。
    // 为简化演示，我们假设只要输入了admin/123456就登录成功。
    if (username.value === 'admin' && password.value === '123456') {
      authStore.loginSuccess({ 
        username: username.value, 
        password: password.value, // 再次强调，生产环境绝不能存明文密码
        roles: ['ROLE_ADMIN', 'ROLE_USER']
      });
      router.push('/users'); // 登录成功后跳转
    } else {
      throw new Error('用户名或密码错误');
    }
  } catch (e) {
    error.value = e.message;
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container { max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ccc; }
/* ... 其他样式 ... */
</style>