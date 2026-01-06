import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/pages/index/PlayGround.vue'),
      beforeEnter: (to, from, next) => {
        const authStore = useAuthStore()
        if (authStore.isLoggedIn && authStore.user?.role === 'admin') {
          next('/admin')
        } else {
          next()
        }
      }
    },
    {
      path: '/account',
      name: 'account',
      component: () => import('../views/pages/account/account.vue'),
      redirect: '/account/login',
      children: [
        {
          path: 'login',
          name: 'login',
          component: () => import('../views/pages/account/login.vue')
        },
        {
          path: 'register',
          name: 'register',
          component: () => import('../views/pages/account/register.vue')
        }
      ]
    },
    {
      path: '/courses',
      name: 'courses',
      component: () => import('../views/pages/courses/CourseList.vue')
    },
    {
      path: '/course/:id',
      name: 'courseDetail',
      component: () => import('../views/pages/courses/CourseDetail.vue'),
      props: true
    },
    {
      path: '/teacher',
      name: 'teacher',
      component: () => import('../views/pages/teacher/TeacherDashboard.vue')
    },
    {
      path: '/teacher/courses/create',
      name: 'courseCreate',
      component: () => import('../views/pages/teacher/CourseCreate.vue')
    },
    {
      path: '/teacher/courses/:id/edit',
      name: 'courseEdit',
      component: () => import('../views/pages/teacher/CourseEdit.vue'),
      props: true
    },
    {
      path: '/teacher/courses/:id/outline',
      name: 'courseOutline',
      component: () => import('../views/pages/teacher/CourseOutline.vue'),
      props: true
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/pages/Profile.vue')
    },
    {
      path: '/recharge',
      name: 'recharge',
      component: () => import('../views/pages/account/Recharge.vue')
    },
      {
        path: '/my-courses',
        name: 'myCourses',
        component: () => import('../views/pages/account/MyCourses.vue')
      },
      {
        path: '/my-favorites',
        name: 'myFavorites',
        component: () => import('../views/pages/account/MyFavorites.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/cart',
        name: 'Cart',
        component: () => import('../views/pages/cart/CartPage.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('../views/pages/order/OrderList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/order/:orderNo',
        name: 'OrderDetail',
        component: () => import('../views/pages/order/OrderDetail.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/learning/:courseId',
        name: 'learning',
        component: () => import('../views/pages/learning/LearningPage.vue')
      },
      {
        path: '/admin',
        name: 'admin',
        component: () => import('../views/pages/admin/AdminDashboard.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
    {
      path: '/404',
      name: 'notFound',
      component: () => import('../views/pages/error/NotFound.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404'
    }
  ]
})

export default router
