<template>
  <div id="login_paper">
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="rules"
      class="login_container"
      label-position="left"
      label-width="0px"
      v-loading="loading"
      @keyup.enter.native="comparePicCode('loginForm')"
    >
      <h3 class="login_title">系统登录</h3>
      <el-form-item prop="username">
        <el-input
          type="text"
          v-model="loginForm.username"
          auto-complete="off"
          placeholder="账号"
        ></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          type="password"
          v-model="loginForm.password"
          auto-complete="off"
          placeholder="密码"
        ></el-input>
      </el-form-item>
      <el-form-item prop="code" style="position: relative; top: -10px">
        <el-input
          type="text"
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 227px"
        ></el-input>
        <img :src="imgSrc" @click="getCode" class="img_code" />
      </el-form-item>

      <el-checkbox
        class="login_remember"
        v-model="checked"
        label-position="left"
        style="margin-bottom: 0px; position: relative; top: -15px"
      >
        记住密码
      </el-checkbox>

      <el-form-item style="width: 100%; text-align: center">
        <el-button
          type="primary"
          @click.native.prevent="comparePicCode('loginForm')"
          style="width: 40%"
        >
          登录
        </el-button>
        <el-button type="primary" @click.native.prevent="submitClick" style="width: 40%">
          注册
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      loginForm: {
        username: '',
        password: '',
        code: ''
      },
      checked: false,
      imgSrc: '',
      loading: false
    }
  },
  mounted() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      window.localStorage.removeItem('loginVerifyCodeKey')
      let loginVerifyCodeKey = Date.now() + Math.ceil(Math.random() * 100)
      //使用 localStorage 创建一个本地存储的 name/value 对
      window.localStorage.setItem('loginVerifyCodeKey', loginVerifyCodeKey)
      this.imgSrc = window.location.origin + '/api' + '/verifyCode/getCode/' + loginVerifyCodeKey // 给图片地址配一个无用的随机数
    },
    // 验证验证码
    comparePicCode(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url =
            '/verifyCode/comparePicCode/' +
            this.loginForm.code +
            '/' +
            window.localStorage.getItem('loginVerifyCodeKey')
          this.$request.get(url).then(res => {
            if (res.data.obj === '正确') {
              this.submitClick()
            } else if (res.data.obj === '错误') {
              this.getCode()
              this.loginForm.code = ''
              this.$message.error('验证码错误')
            } else {
              this.getCode()
              this.loginForm.code = ''
              this.$message.error('验证码已过期')
            }
          })
        } else {
          this.$message.error('请完成输入后再提交')
        }
      })
    },
    // 验证账号密码
    submitClick: function () {
      let that = this
      this.$request.postForm('/securityUser/login', this.loginForm).then(res => {
        if (res.data.obj !== '账号或密码错误') {
          this.$message.success('登录成功')
          window.sessionStorage.setItem('username', res.data.obj.username)
          //将token令牌放入会话存储中
          window.sessionStorage.setItem('token', res.data.obj.token)
          if (this.checked === true) {
            that.setCookie(that.loginForm.username, that.loginForm.password, 7)
          }
          this.$router.push('/home')
        } else {
          this.getCode()
          this.loginForm.username = ''
          this.loginForm.password = ''
          this.loginForm.code = ''
          this.$message.error(res.data.obj)
        }
      })
    },
    //设置cookie
    setCookie(c_name, c_pwd, c_days) {
      let date = new Date() //获取时间
      date.setTime(date.getTime() + 24 * 60 * 60 * 1000 * c_days) //保存的天数
      //字符串拼接cookie
      window.document.cookie = 'userName' + '=' + c_name + ';path=/;expires=' + date.toGMTString()
      window.document.cookie = 'userPwd' + '=' + c_pwd + ';path=/;expires=' + date.toGMTString()
    },
    //读取cookie
    getCookie: function () {
      if (document.cookie.length > 0) {
        let arr = document.cookie.split('; ') //这里显示的格式需要切割一下自己可输出看下
        for (let i = 0; i < arr.length; i++) {
          let arr2 = arr[i].split('=') //再次切割
          //判断查找相对应的值
          if (arr2[0] == 'userName') {
            this.loginForm.username = arr2[1] //保存到保存数据的地方
          } else if (arr2[0] == 'userPwd') {
            this.loginForm.password = arr2[1]
          }
        }
      }
    },
    //清除cookie
    clearCookie: function () {
      this.setCookie('', '', -1) //修改2值都为空，天数为负1天就好了
    }
  }
}
</script>
<style scoped>
#login_paper {
  background: url('../assets/login_bg.jpg') no-repeat center;
  height: 100%;
  width: 100%;
  background-size: cover;
  position: fixed;
}

.login_container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 180px auto;
  width: 400px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}

.login_remember {
  margin: 0px 0px 35px 0px;
  text-align: left;
}

.img_code {
  position: relative;
  top: 12px;
}
</style>
