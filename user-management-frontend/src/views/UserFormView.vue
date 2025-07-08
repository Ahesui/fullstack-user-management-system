<template>
  <div class="user-form">
    <!-- 我们会根据是新增还是编辑，动态显示标题 -->
    <h1>{{ isEditMode ? '编辑用户' : '添加新用户' }}</h1>
    
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">用户名:</label>
        <!-- v-model实现了输入框和data中user对象的双向绑定 -->
        <input type="text" id="username" v-model="user.username" required>
      </div>
      <div class="form-group">
        <label for="email">邮箱:</label>
        <input type="email" id="email" v-model="user.email" required>
      </div>
      <div class="form-group">
        <label for="age">年龄:</label>
        <input type="number" id="age" v-model="user.age">
      </div>
      
      <div class="form-actions">
        <button type="submit">保存</button>
        <!-- 使用<router-link>返回列表页 -->
        <router-link to="/users" class="cancel-button">取消</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import UserService from '@/services/UserService.js';
// 1. 导入useRouter，用于编程式导航
import { useRouter } from 'vue-router';
export default {
  name: 'UserFormView',
  // 1. 接收从路由传来的id prop
  props: {
    id: {
      type: [String, Number], // id可能是字符串或数字
      default: null
    }
  },
  // 2. setup()是Vue 3组合式API的入口，可以和选项式API混用
  setup() {
    const router = useRouter(); // 获取路由实例
    return { router }; // 将router暴露给模板和选项式API
  },
  data() {
    return {
      user: { // 用于表单绑定的数据对象
        username: '',
        email: '',
        age: null
      },
    //   isEditMode: false // 标志位，判断是新增还是编辑模式
    };
  },
  // 3. 【新增】计算属性
  computed: {
    isEditMode() {
      // 如果this.id有值(不为null或undefined)，说明是编辑模式
      return this.id != null;
    }
  },
  methods: {
    handleSubmit() {
        if (this.isEditMode) {
            // 调用createUser API
        UserService.updateUser(this.id, this.user)
        .then(response  => {
             // response.data 就是我们后端的 Result 对象
             const result = response.data;
             // 在.then内部判断业务状态码
            if (result.code === 200) {
                // 业务成功
                alert('用户更新成功！');
                this.router.push('/users');
            } else {
                // 业务失败，但HTTP成功
                console.error('API Error:', result.message);
                alert(`更新失败：${result.message}`);
            }   
        })
        .catch(error => {
        
          console.error('Error creating user:', error);
          // 如果后端返回了校验错误信息
          if (error.response && error.response.data && error.response.data.message) {
            alert(`更新失败：${error.response.data.message}`);
          } else {
            alert(`更新失败：${error.message}`);
          }
        });
        } else {
            // 调用createUser API
      UserService.createUser(this.user)
        .then(response  => {
             // response.data 就是我们后端的 Result 对象
             const result = response.data;
             // 在.then内部判断业务状态码
            if (result.code === 200) {
                // 业务成功
                alert('用户添加成功！');
                this.router.push('/users');
            } else {
                // 业务失败，但HTTP成功
                console.error('API Error:', result.message);
                alert(`添加失败：${result.message}`);
            }   
        })
        .catch(error => {
        
          console.error('Error creating user:', error);
          // 如果后端返回了校验错误信息
          if (error.response && error.response.data && error.response.data.message) {
            alert(`添加失败：${error.response.data.message}`);
          } else {
            alert(`添加失败：${error.message}`);
          }
        });
        }
      
         console.log('Form submitted with data:', this.user);
    },// 4. 【新增】加载用户数据的方法
    fetchUser() {
      UserService.getUserById(this.id)
        .then(response => {
          // response.data 是 Result<User>, response.data.data 是 User 对象
          this.user = response.data.data;
        })
        .catch(error => {
          console.error('Error fetching user for edit:', error);
          alert('加载用户信息失败！');
          this.router.push('/users'); // 加载失败则返回列表
        });
    }
  },
  // 5. 修改created生命周期钩子
  created() {
    // 如果是编辑模式，就在组件创建时加载用户数据
    if (this.isEditMode) {
      this.fetchUser();
    }
  },

}
</script>

<style scoped>
.user-form { max-width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
.form-group { margin-bottom: 15px; }
label { display: block; margin-bottom: 5px; }
input { width: 100%; padding: 8px; box-sizing: border-box; }
.form-actions { margin-top: 20px; }
.cancel-button { margin-left: 10px; }
</style>