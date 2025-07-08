<template>
  <div>
    <h2>登录</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="username" type="text" placeholder="用户名" required>
      <input v-model="password" type="password" placeholder="密码" required>
      <button type="submit">登录</button>
      <p v-if="error">{{ error }}</p>
    </form>
  </div>
</template>
<script>
import { ref } from 'vue';
import { useAuthStore } from '@/store/auth2';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  setup() {
    const username = ref('admin'); // 默认值方便测试
    const password = ref('123456');

    const error = ref(null);
    const authStore = useAuthStore();
    const router = useRouter();

    const handleLogin = async () => {
      try {
        error.value = null;
        // 使用axios直接调用，因为我们需要手动构造Basic Auth
        console.log('env value:',`${process.env.VUE_APP_API_BASE_URL}`);
        // const response = await axios.post(
        //   `${process.env.VUE_APP_API_BASE_URL}/auth/login`, 
        //   {}, // POST请求体为空
        //   {
        //     auth: {
        //       username: username.value,
        //       password: password.value,
        //     },
        //   }
        // );
        const response = await axios.post(
           `${process.env.VUE_APP_API_BASE_URL}/auth/login`, 
          // 【重要】将用户名和密码作为请求体发送
          {
            username: username.value,
            password: password.value,
          }
          // 不再需要auth配置
        );

        // 登录成功，调用store的action
        authStore.loginSuccess(response.data);
        // 跳转到用户列表页
        router.push('/users');
      } catch (err) {
        error.value = '用户名或密码错误。';
        console.error('Login failed:', err);
      }
    };
    return { username, password, error, handleLogin };
  }
}
</script>