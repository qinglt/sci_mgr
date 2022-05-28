<template>
  <el-container>
    <side-bar></side-bar>

    <el-container>
      <el-main>
        <el-button type="primary" @click="test01">测试1</el-button>
        <el-button type="primary" @click="test02">测试2</el-button>
        <el-button type="primary" @click="test03">测试3</el-button>
        <el-button type="primary" @click="logout">注销</el-button>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import SideBar from '../components/sideBar.vue'
export default {
  name: 'home',
  components: { SideBar },
  data() {
    return {}
  },
  mounted() {},
  methods: {
    test01() {
      console.log(window.sessionStorage.getItem('token'))
      console.log(window.localStorage.getItem('token'))
      this.$request
        .post('/hello', {
          username: 'admin',
          password: '123456'
        })
        .then(res => {
          if (res.data.code === 200) {
            this.$message.success('测试1成功')
          }
        })
    },
    test02() {
      this.$request.get('/test02').then(res => {
        if (res.data.code === 599) {
          this.$message.error(res.data.obj)
        }
      })
    },
    test03() {
      window.open(window.location.origin + '/api' + '/download/111.docx')
    },
    logout() {
      this.$request.get('/securityUser/logout').then(res => {
        if (res.data.code === 200) {
          window.sessionStorage.removeItem('token')
          this.$message.success('注销成功')
          this.$router.push('/')
        }
      })
    }
  }
}
</script>

<style scoped></style>
