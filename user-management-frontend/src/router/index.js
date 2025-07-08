import { createRouter, createWebHistory } from 'vue-router'
import UserListView from '../views/UserListView.vue'
// 1. 导入新组件
import UserFormView from '../views/UserFormView.vue'
import { useAuthStore } from '@/store/auth2'; // 1. 导入我们的auth store
import LoginView from '../views/LoginView2.vue'; // 2. 导入登录视图



const routes = [
  {
    path: '/',
    name: 'user-list-redirect',
    redirect: '/users'
  },
  {
    path: '/users',
    name: 'user-list',
    component: UserListView,
    // meta: { requiresAuth: true } // 3. 添加元信息，标记这个路由需要认证
  },
  // 2. 添加新增用户的路由
  {
    path: '/users/new',
    name: 'user-new',
    component: UserFormView,
    // meta: { requiresAuth: true }
  },
  // 【新增】编辑用户的路由，:id是一个动态路径参数
  {
    path: '/users/edit/:id', // :id 会匹配/users/edit/1, /users/edit/2等
    name: 'user-edit',
    component: UserFormView,
    props: true, // 允许将路由参数作为props传递给组件
    // meta: { requiresAuth: true }
  },
  {
    path: '/login', // 4. 添加登录页的路由
    name: 'login',
    component: LoginView,
    // meta: { guestOnly: true } // 标记这个路由只对未登录用户开放
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})


// // 5. 创建全局前置守卫
// router.beforeEach((to, from, next) => {
//   // Pinia store必须在守卫函数内部获取，因为此时Pinia实例才被激活
//   const authStore = useAuthStore();
  
//   const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
//   const guestOnly = to.matched.some(record => record.meta.guestOnly);

//   if (requiresAuth && !authStore.isAuthenticated) {
//     // 如果目标路由需要认证，但用户未登录
//     // 则重定向到登录页
//     next({ name: 'login' });
//   } else if (guestOnly && authStore.isAuthenticated) {
//     // 如果目标路由只对未登录用户开放（如登录页），但用户已登录
//     // 则重定向到主页
//     next({ path: '/users' });
//   } else {
//     // 其他所有情况，正常放行
//     next();
//   }
// });

// 2. 【关键】添加全局前置路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const publicPages = ['/login'];
  const authRequired = !publicPages.includes(to.path);

  if (authRequired && !authStore.isAuthenticated) {
    // 如果访问的页面需要认证，但用户未登录，则强制跳转到登录页
    return next('/login');
  }
  // 否则，正常放行
  next();
});


export default router
