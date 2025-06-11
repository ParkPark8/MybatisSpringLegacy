#MybatisSpringLegacy

> Mybatis, Spring 기본 설정 파일



#  1. `RootConfig.java` – 루트 애플리케이션 설정 (비즈니스 로직, DB 설정 등)

```java
@Configuration
@PropertySource({"classpath:/application.properties"})
@MapperScan(basePackages = {})
```

### 💡 클래스의 역할

* **Spring의 루트 컨텍스트 설정 클래스**로서, DB 연결, 트랜잭션 처리, MyBatis 설정 등을 담당
* DispatcherServlet에 의해 로딩되는 서블릿 컨텍스트와는 별도로, **비즈니스 로직 관련 설정**을 포함

---

### 주요 필드

```java
@Autowired
ApplicationContext applicationContext;
```

* 현재 ApplicationContext를 주입 받아서 Bean 생성 시 resource 등의 접근에 사용

```java
@Value("${jdbc.driver}") String driver;
@Value("${jdbc.url}") String url;
@Value("${jdbc.username}") String username;
@Value("${jdbc.password}") String password;
```

* `application.properties`로부터 설정값을 주입 받음 (`@PropertySource`로 등록된 경로 기준)

---

### ① DataSource 설정

```java
@Bean
public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(driver);
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);

    HikariDataSource dataSource = new HikariDataSource(config);
    return dataSource;
}
```

#### 설명:

* **HikariCP**: 가장 빠르고 가벼운 커넥션 풀 라이브러리
* DB 연결을 위한 기본 객체 설정
* 이 `dataSource`는 나중에 MyBatis 및 트랜잭션 처리에서 재사용됨

---

### ② SqlSessionFactory 설정

```java
@Bean
public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
    sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
    sqlSessionFactory.setDataSource(dataSource());
    return sqlSessionFactory.getObject();
}
```

#### 설명:

* MyBatis의 핵심 구성 요소인 `SqlSessionFactory`를 Bean으로 등록
* `setConfigLocation`: MyBatis 설정 파일(xml) 경로 지정
* `setDataSource`: 위에서 생성한 HikariDataSource를 사용

---

### ③ 트랜잭션 매니저 설정

```java
@Bean
public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
}
```

#### 설명:

* 트랜잭션 처리를 위한 스프링 전용 매니저
* `@Transactional` 사용을 위한 기반 설정

---

### ④ `@MapperScan` 주석

```java
@MapperScan(basePackages = {})
```

* **MyBatis의 Mapper 인터페이스 자동 스캔 기능**
* 여기에 패키지를 지정하면 Mapper 인터페이스가 빈으로 자동 등록됨
* 이 코드에서는 비어 있으므로, **추가적으로 설정 필요**

---

# ✅ 2. `ServletConfig.java` – 웹 MVC 설정

```java
@EnableWebMvc
@ComponentScan(basePackages = {
    "org.scoula.controller", 
    "org.scoula.exception", 
    "org.scoula.ex03.controller"
})
```

### 💡 클래스의 역할

* Spring MVC 관련 설정 담당 (ViewResolver, 정적 자원 설정, Multipart 업로드 등)
* **웹 요청을 처리할 Controller**, 예외 처리 클래스 등을 스캔하도록 지정

---

### ① 정적 리소스 처리

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**")
            .addResourceLocations("/resources/");
}
```

#### 설명:

* 정적 파일(js, css, 이미지 등)을 `/resources/` 하위 URL로 접근할 수 있게 매핑

---

### ② ViewResolver 설정

```java
@Override
public void configureViewResolvers(ViewResolverRegistry registry) {
    InternalResourceViewResolver bean = new InternalResourceViewResolver();
    bean.setViewClass(JstlView.class);
    bean.setPrefix("/WEB-INF/views/");
    bean.setSuffix(".jsp");
    registry.viewResolver(bean);
}
```

#### 설명:

* 컨트롤러에서 반환한 뷰 이름을 `.jsp` 파일로 연결
* 예: `"home"` → `/WEB-INF/views/home.jsp`

---

### ③ 파일 업로드 처리

```java
@Bean
public MultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
}
```

#### 설명:

* **Servlet 3.0 기반 파일 업로드 지원**
* HTML 폼의 `enctype="multipart/form-data"`를 처리할 때 필요

---

# ✅ 3. `WebConfig.java` – DispatcherServlet 등록 및 초기화

이 클래스는 `web.xml`을 대체합니다. 즉, **톰캣 구동 시 가장 먼저 실행되는 설정 파일**입니다.

```java
public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer
```

---

### ① customizeRegistration

```java
@Override
protected void customizeRegistration(ServletRegistration.Dynamic registration) {
    registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    MultipartConfigElement multipartConfig =
        new MultipartConfigElement(
            LOCATION,
            MAX_FILE_SIZE,
            MAX_REQUEST_SIZE,
            FILE_SIZE_THRESHOLD
        );
    registration.setMultipartConfig(multipartConfig);
}
```

#### 설명:

* 요청된 URL이 매핑되지 않은 경우 404 예외를 던지게 설정
* 파일 업로드의 한계값 설정: 경로, 단일 파일 크기, 전체 크기 등

---

### ② DispatcherServlet 설정

```java
@Override
protected Class<?>[] getRootConfigClasses() {
    return new Class[] {RootConfig.class};
}

@Override
protected Class<?>[] getServletConfigClasses() {
    return new Class[] {ServletConfig.class};
}

@Override
protected String[] getServletMappings() {
    return new String[]{"/"};
}
```

#### 설명:

* `RootConfig`: DB 및 비즈니스 관련 설정
* `ServletConfig`: 웹 MVC 설정
* `/`: 모든 요청은 DispatcherServlet이 처리

---

### ③ 인코딩 필터

```java
@Override
protected Filter[] getServletFilters() {
    CharacterEncodingFilter filter = new CharacterEncodingFilter();
    filter.setEncoding("UTF-8");
    filter.setForceEncoding(true);
    return new Filter[] { filter };
}
```

#### 설명:

* POST 방식의 한글 깨짐을 방지하는 인코딩 필터 설정

---

# ✅ 4. `application.properties`

```properties
# 실제 드라이버
#jdbc.driver=com.mysql.cj.jdbc.Driver
#jdbc.url=jdbc:mysql://127.0.0.1:3306/scoula_db

# log4jdbc를 통한 SQL 로그 출력용 드라이버
jdbc.driver=net.sf.log4jdbc.sql.jdbcapi.DriverSpy
jdbc.url=jdbc:log4jdbc:mysql://localhost:3306/scoula_db

jdbc.username=scoula
jdbc.password=1234
```

### 설명:

* Spring이 사용할 DB 접속 정보 설정
* `log4jdbc` 사용 시 SQL 로그를 콘솔에 출력 (디버깅에 유용)

---

# ✅ 5. `mybatis-config.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
</configuration>
```

### 설명:

* MyBatis의 전역 설정 파일
* 현재는 비어 있지만, 다음 항목들을 설정할 수 있음:

#### 예시로 추가 가능한 설정

```xml
<settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>

<typeAliases>
    <typeAlias alias="User" type="com.example.model.User"/>
</typeAliases>

<plugins>
    <plugin interceptor="com.example.MyInterceptor"/>
</plugins>
```

---

# ✅ 정리 요약

| 파일명                        | 역할                                                  |
| -------------------------- | --------------------------------------------------- |
| **RootConfig**             | DB, MyBatis, 트랜잭션 설정                                |
| **ServletConfig**          | Spring MVC: View, Resource, Multipart 설정            |
| **WebConfig**              | DispatcherServlet, Encoding Filter, Multipart 제한 설정 |
| **application.properties** | DB 연결정보 설정                                          |
| **mybatis-config.xml**     | MyBatis 전역 설정 (현재는 비어있지만 확장 가능)                     |

---
