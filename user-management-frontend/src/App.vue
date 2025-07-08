<script>
import { onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '@/store/auth2';
import { useRouter } from 'vue-router';

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    const handleStorageChange = (event) => {
      // 我们只关心'token'这个key的变化
      if (event.key === 'token') {
        console.log('Storage event detected for key "token"');

        // 如果newValue是null，说明在其他页面登出了
        if (event.newValue === null) {
          console.log('Token removed from another tab. Logging out.');
          // 手动更新当前页面的store状态
          authStore.logout(); 
          // 跳转到登录页
          router.push('/login');
        } else {
          // 如果是token被设置或更新（比如实现了自动续签），也可以在这里处理
          // 为了简单，我们暂时只处理登出
        }
      }
    };

    // onMounted是Vue 3组合式API的生命周期钩子，在组件挂载到DOM后执行
    onMounted(() => {
      // 添加事件监听器
      window.addEventListener('storage', handleStorageChange);
      console.log('Storage event listener added.');
    });

    // onUnmounted在组件被卸载前执行
    onUnmounted(() => {
      // 移除事件监听器，防止内存泄漏
      window.removeEventListener('storage', handleStorageChange);
      console.log('Storage event listener removed.');
    });

    // setup函数不需要返回任何东西，因为我们只是在设置副作用
  }
}
</script>

<template>
  <router-view/>
</template>

<style>
/* 这里可以放一些全局样式 */
body {
  margin: 0;
  font-family: Avenir, Helvetica, Arial, sans-serif;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
