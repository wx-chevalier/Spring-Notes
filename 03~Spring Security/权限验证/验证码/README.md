# Captcha | 验证码

```xml
<dependencies>
    <dependency>
        <groupId>com.github.axet</groupId>
        <artifactId>kaptcha</artifactId>
        <version>0.0.9</version>
    </dependency>
</dependencies>
```

```java
@Controller
public class RegisterKaptchaController extends KaptchaExtend {

  @RequestMapping(value = "/captcha.jpg", method = RequestMethod.GET)
  public void captcha(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    super.captcha(req, resp);
  }

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public ModelAndView registerGet(
    @RequestParam(value = "error", required = false) boolean failed,
    HttpServletRequest request
  ) {
    ModelAndView model = new ModelAndView("register-get");

    //
    // model MUST contain HTML with <img src="/captcha.jpg" /> tag
    //
    return model;
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ModelAndView registerPost(
    @RequestParam(value = "email", required = true) String email,
    @RequestParam(value = "password", required = true) String password,
    HttpServletRequest request
  ) {
    ModelAndView model = new ModelAndView("register-post");

    if (email.isEmpty()) throw new RuntimeException("email empty");

    if (password.isEmpty()) throw new RuntimeException("empty password");

    String captcha = request.getParameter("captcha");

    if (!captcha.equals(getGeneratedKey(request))) throw new RuntimeException(
      "bad captcha"
    );

    //
    // eveyting is ok. proceed with your user registration / login process.
    //
    return model;
  }
}
```

```xml
<!-- 配置验证码 -->
<bean id="captchaProducer" class="com.google.code.kaptcha.impl.DefaultKaptcha">
    <property name="config">
        <bean class="com.google.code.kaptcha.util.Config">
            <constructor-arg>
                <props>
                    <!-- 图片边框 -->
                    <prop key="kaptcha.border">no</prop>
                    <!-- 图片宽度 -->
                    <prop key="kaptcha.image.width">95</prop>
                    <!-- 图片高度 -->
                    <prop key="kaptcha.image.height">45</prop>
                    <!-- 验证码背景颜色渐变，开始颜色 -->
                    <prop key="kaptcha.background.clear.from">248,248,248</prop>
                    <!-- 验证码背景颜色渐变，结束颜色 -->
                    <prop key="kaptcha.background.clear.to">248,248,248</prop>
                    <!-- 验证码的字符 -->
                    <prop key="kaptcha.textproducer.char.string">0123456789abcdefghijklmnopqrstuvwxyz快过年了我想回家</prop>
                    <!-- 验证码字体颜色 -->
                    <prop key="kaptcha.textproducer.font.color">0,0,255</prop>
                    <!-- 验证码的效果，水纹 -->
                    <prop key="kaptcha.obscurificator.impl">com.google.code.kaptcha.impl.WaterRipple</prop>
                    <!-- 验证码字体大小 -->
                    <prop key="kaptcha.textproducer.font.size">35</prop>
                    <!-- 验证码字数 -->
                    <prop key="kaptcha.textproducer.char.length">4</prop>
                    <!-- 验证码文字间距 -->
                    <prop key="kaptcha.textproducer.char.space">2</prop>
                    <!-- 验证码字体 -->
                    <prop key="kaptcha.textproducer.font.names">new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)</prop>
                    <!-- 不加噪声 -->
                    <prop key="kaptcha.noise.impl">com.google.code.kaptcha.impl.NoNoise</prop>
                </props>
            </constructor-arg>
        </bean>
    </property>
</bean>
```
