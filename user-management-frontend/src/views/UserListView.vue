<template>
  <div class="actions">
    <button @click="handleLogout" class="logout-button">退出登录</button>
  </div>
  <div class="user-list">
    <h1>用户列表</h1>
    
    <!-- 添加一个加载状态的提示 -->
    <div v-if="loading">正在加载数据...</div>

    <!-- 添加一个错误状态的提示 -->
    <div v-if="error" class="error-message">加载失败：{{ error }}</div>
<div class="actions">
    <!-- 使用 <router-link> 来进行页面跳转 -->
    <router-link to="/users/new">
      <button>添加新用户</button>
    </router-link>
  </div>
    <!-- 只有在数据加载成功并没有错误时，才显示表格 -->
    <table v-if="!loading && !error">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>邮箱</th>
          <th>年龄</th>
          <th>注册时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <!-- v-for 是Vue的循环指令，类似于Thymeleaf的th:each -->
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.age }}</td>
          <!-- 我们会用一个方法来格式化日期 -->
          <td>{{ formatDateTime(user.createTime) }}</td>
          <td>
            <!-- 【新增】编辑按钮 -->
            <router-link :to="`/users/edit/${user.id}`">
              <button>编辑</button>
            </router-link>
            <!-- 删除按钮 -->
            <!-- @click是v-on:click的简写，用于绑定点击事件 -->
            <button @click="handleDelete(user.id, user.username)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
// 导入我们创建的API服务
import UserService from '@/services/UserService.js';
// 同样需要导入 useRouter 和 useAuthStore
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth2';

export default {
  name: 'UserListView',
  data() {
    // data()函数返回一个对象，用于存放组件的响应式状态数据
    return {
      users: [], // 存放用户列表的数组
      loading: true, // 加载状态标志
      error: null // 存放错误信息
    };
  },
  methods: {
    // methods对象用于定义组件的方法
    fetchUsers() {
      this.loading = true;
      this.error = null;
      
      UserService.getUsers() // 调用API
        .then(response => {
          // 请求成功，response.data 就是我们后端Result<T>中的data部分
          this.users = response.data.data.list; // 从PageInfo中获取列表
          console.log('Users fetched:', this.users);
        })
        .catch(error => {
          // 请求失败
          console.error('Error fetching users:', error);
          this.error = error.message;
        })
        .finally(() => {
          // 无论成功或失败，最后都结束加载状态
          this.loading = false;
        });
    },
    /**
   * 处理删除按钮的点击事件
   * @param {number} userId - 要删除的用户ID
   * @param {string} username - 用于在确认框中显示用户名
   */
  handleDelete(userId, username) {
    // window.confirm会弹出一个浏览器原生的确认框
    if (window.confirm(`您确定要删除用户 "${username}" 吗？`)) {
      UserService.deleteUser(userId)
        .then(() => {
          // 删除成功
          alert('用户删除成功！');
          // 重新获取用户列表，以刷新页面
          this.fetchUsers();
        })
        .catch(error => {
          // 删除失败
          console.error('Error deleting user:', error);
          // 使用alert显示一个简单的错误提示
          alert(`删除失败：${error.response?.data?.message || error.message}`);
        });
    }
  },
    formatDateTime(dateTime) {
      if (!dateTime) return '';
      return new Date(dateTime).toLocaleString();
    }
  },
  created() {
    // created() 是Vue的一个生命周期钩子，在组件实例被创建后立即调用
    // 我们在这里调用获取数据的方法
    this.fetchUsers();
  },
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    // 将方法也暴露出去
    const handleLogout = () => {
      authStore.logout();
      router.push({ name: 'login' });
    };
    return { router, handleLogout };
  }
}
</script>

<style scoped>
/* 操作区容器样式 */
.actions {
  display: flex; /* 使用 Flexbox 布局 */
  justify-content: flex-end; /* 让子元素靠右对齐 */
  margin-bottom: 20px; /* 和下面的表格拉开一些间距 */
}
.user-list {
  width: 80%;
  margin: 20px auto;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
th {
  background-color: #f2f2f2;
}
.error-message {
  color: red;
}
</style>