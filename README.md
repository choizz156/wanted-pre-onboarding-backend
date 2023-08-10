
# 👉 Wanted-pre-onboarding-backend 과제



## 👩🏽‍💻 Author

### 🌱 최민석 | 백엔드


- 커뮤니케이션과 피드백을 통해 부족한 점을 빠르게 파악하고 보완하려고 노력합니다!
- 주어진 환경에 빠르게 적응할 수 있습니다! 
- 유지/보수하기 좋은 설계를 추구하고 쉽게 읽히는 코드를 짜려고 고민합니다!



## 📌 Stack

**Server:** 
- Java 17
- Spring boot 3.3.2, Gradle 8.2.1 
- Spring Security 7.0, JWT 0.11.5 
- Spring Data JPA, QueryDsl , MySQL 8.0
- Spring Rest Docs
- AWS (EC2, RDS), Docker


## 🚀 Architecture
- **VPC를 설정하고, subnetting을** 통해 네트워크를 효율적으로 관리합니다.
    - public subnet으로 관리망(Bastion), 서비스망(Service), private subnet으로 RDS를 관리합니다.
- **Bastion Server**
    - Bastion Server를 통해 서비스의 정상 트래픽과 관리자용 트랙픽을 구분할 수 있게 했습니다.
    - 22번 포트의 보안이 뚫린다면 서비스에 심각한 문제가 발생할 수 있기 때문에 Bastion Server를 통해 서비스에 영향을 최소화하게 했습니다.

![](https://github.com/choizz156/choizz156/blob/60ce9c63985b8db12a4d3dbbf67cd454689db855/imagesFile/aws%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A7.png)

- **letsencrypt를 사용한 Https 적용**
    - docker와 nignx를 통한 `reverse proxy`를 적용해, TLS를 설정했습니다.
    - 80 포트, 443 포트를 통해 들어오는 모든 요청은 https 요청으로 리다이렉트 합니다.
        - `docker run -d --rm --name proxy -p 80:80 -p 443:443 -v '/etc/letsencrypt:/etc/letsencrypt' reverse-proxy`

![](https://github.com/choizz156/choizz156/blob/60ce9c63985b8db12a4d3dbbf67cd454689db855/imagesFile/web%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A7.png)
  </br>
  <details>
  <summary> # DockerFileNginx </summary>  
  <div markdown="1">

  ```docker
  FROM nginx:stable

  COPY nginx.conf /etc/nginx/nginx.conf

  ```
  </div>
  </details>

  <details>
  <summary> # nginx.conf</summary>
  <div markdown="1">

  ```nginx
  events {}

  http {       
    upstream app {
      server 172.17.0.1:8080;
    }
  
    # Redirect all traffic to HTTPS
    server {
      listen 80;
      return 301 https://$host$request_uri;
    }

    server {
      listen 443 ssl;  
      ssl_certificate /etc/letsencrypt/live/choizz-onboarding.p-e.kr/fullchain.pem;
      ssl_certificate_key /etc/letsencrypt/live/choizz-onboarding.p-e.kr/privkey.pem;

      ssl_protocols TLSv1 TLSv1.1 TLSv1.2;

      ssl_prefer_server_ciphers on;
      ssl_ciphers ECDH+AESGCM:ECDH+AES256:ECDH+AES128:DH+3DES:!ADH:!AECDH:!MD5;

      add_header Strict-Transport-Security "max-age=31536000" always;

      ssl_session_cache shared:SSL:10m;
      ssl_session_timeout 10m;      

      location / {
        proxy_pass http://app;    
      }
    }
  }

  ```

  </div>
  </details>

---

## 🚀 ERD
![](https://github.com/choizz156/choizz156/blob/60ce9c63985b8db12a4d3dbbf67cd454689db855/imagesFile/onboarderd.png)

---

## 🖥 Run App
### docker-comopose 사용 (Local)
- 로컬 환경에서 docker-compose를 이용해 애플리케이션을 수행할 수 있습니다.
- git clone을 받습니다.
    - 데이터베이스 비밀번호 등의 보안을 위해 `git submodules`을 사용했습니다. 따라서, clone시 `--recurse-submodules`을 붙여줘야 합니다.
    - `git clone --recurse-submodules https://github.com/choizz156/wanted-pre-onboarding-backend.git`

- 해당 레포지토리로 이동 후 쉘 스크립트를 실행하거나, docker-compose 명령어를 사용합니다.

```bash
./run.sh

#docker-compose -p pre-onboarding-backend --env-file src/main/resources/config/.env up -d

```
- http://localhost:8081 주소를 사용합니다.
- POST, GET , DELETE 등 HTTP 메서드를 사용하여 엔드포인드를 호출할 수 있습니다.
- API 문서를 참고해 주세요! (postman 사용을 추천합니다)

<details>
<summary> # DockerFile</summary>
<div markdown="1">

```yml
FROM gradle:jdk17 AS build-stage

WORKDIR /app

COPY --chown=gradle:gradle . /app

RUN ./gradlew clean build

FROM eclipse-temurin:17.0.8_7-jdk-focal

WORKDIR /app

ARG JAR_FILE="build/libs/*.jar"
COPY --from=build-stage /app/$JAR_FILE app.jar

ARG PROFILE="local"
ENV SPRING_PROFILES_ACTIVE=$PROFILE

ENTRYPOINT ["java", "-jar", "app.jar"]
```

</div>
</details>

<details>
<summary> # docker-compose.yml</summary>
<div markdown="1">

```yml
version: "3.8"

services:
  db:
    image: mysql:8
    volumes:
      - ./db-data:/var/lib/mysql
    restart: always
    ports:
      - '13306:3306'
    command:
      - '--character-set-server=utf8mb4'
      - '--collation-server=utf8mb4_unicode_ci'
    environment:
      MYSQL_ROOT_PASSWORD: ${PASSWORD}
      MYSQL_DATABASE: onboarding
      MYSQL_USER: choizz
      MYSQL_PASSWORD: ${PASSWORD}
      TZ: Asia/Seoul


  db-admin:
    container_name: db-adminer
    image: adminer:latest
    ports:
      - "18080:8080"
    environment:
      - ADMINER_DEFAULT_SERVER=db
      - ADMINER_DESIGN=hydra
      - ADMINER_PLUGINS=tables-filter tinymce

  app:
    container_name: onboarding-app
    build: ./
    restart: always
    ports:
      - '8081:8080'
    depends_on:
      - db
```

</div>
</details>

---

## 🔗 Development
### 공통 적용 기능
- JPA 
    - 반복적인 CRUD 쿼리를 반복해서 작성할 필요가 없습니다.
    - 객체끼리의 관계 표현이 쉽고, 비지니스 로직에 집중할 수 있습니다.
    - 코드의 가독성이 높아지고 유지 보수 및 리팩토링에 유리합니다.
- Restful API를 만들기 위해 Spring Boot MVC를 사용했습니다.
- 구체적인 비지니스 로직 예외를 두기 위해 RuntimeExcpetion을 상속하여 BusinessLoginException을 따로 만들어 사용했습니다.
- 불변 객체 타입인 `record`를 사용하여 Dto를 구현했습니다.

 ### 1️⃣ 사용자 회원가입 엔드 포인트
- `POST https://choizz-onboarding.p-e.kr/users`

 #### (1) 회원 가입
- UserController에서 email과 password를 Request Body로 받습니다.
- UserService에서 email과 password를 db에 저장합니다. password는 암호화(Spring Security의 PasswordEncoder)하여 db에 저장합니다.
  ![](https://github.com/choizz156/choizz156/blob/60ce9c63985b8db12a4d3dbbf67cd454689db855/imagesFile/password.png)
- 만약, 동일한 email이 존재한다면 커스터마이징한 예외(EXIST_EMAIL)를 던집니다.

<details>
<summary> # Exception Code</summary>
<div markdown="1">

```java
@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    EXIST_EMAIL(400, "이미 가입한 e-mail입니다."),
    NOT_FOUND_USER(400, "찾을 수 없는 회원입니다."),
    NOT_FOUND_POST(400, "찾을 수 없는 게시글입니다." ),
    NOT_MATCHING_OWNER(403, "작성자와 동일한 회원이 아닙니다." );

    private final int code;
    private final String msg;
}

```
</div>
</details>

#### (2) email과 password 유효성 검사
- Spring Validation을 이용하여 Request가 UserController로 들어올 때, 유효성 검사를 합니다.
- Spring Validation에서 검증을 위해 제공하는 애노테이션이 있지만 요구 사항에 맞게 하기 위해 정규표현식을 사용했습니다.
- 만약 유효하지 않는다면, 표준 에러 응답 객체를 활용하여 MethodArgumentNotValidException 예외를 던집니다.
    - 예외가 발생할 경우, Spring의 Exception Handler를 사용하여 예외를 처리합니다. 
    - 객체 생성에 필요한 파라미터가 다르기 때문에, `정적 팩토리 메서드`를 사용해 에러 응답 객체를 생성할 수 있게 했습니다.
<details>
<summary> # ErrorResponse</summary>
<div markdown="1">

```java
@Getter
@JsonInclude(Include.NON_EMPTY)
public class ErrorResponse {

    private final int status;
    private final String msg;
    private final List<CustomFieldError> customFieldErrors;

    @Builder
    private ErrorResponse(int status, String msg, List<CustomFieldError> customFieldErrors) {
        this.status = status;
        this.msg = msg;
        this.customFieldErrors = customFieldErrors;
    }

    public static ErrorResponse of(HttpStatus httpStatus, BindingResult bindingResult) {
        return ErrorResponse.builder()
            .status(httpStatus.value())
            .customFieldErrors(CustomFieldError.of(bindingResult))
            .build();
    }

    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return ErrorResponse.builder()
            .status(exceptionCode.getCode())
            .msg(exceptionCode.getMsg())
            .build();
    }

    public static ErrorResponse of(HttpStatus httpStatus, String msg) {
        return ErrorResponse.builder()
            .status(httpStatus.value())
            .msg(msg)
            .build();
    }

    public static ErrorResponse of(String msg) {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .msg(msg)
            .build();
    }

    @Getter
    public static class CustomFieldError {

        private String field;
        private Object rejectedValue;
        private String reason;

        private CustomFieldError(
            final String field,
            final Object rejectedValue,
            final String reason
        ) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<CustomFieldError> of(BindingResult bindingResult) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                .map(error ->
                    new CustomFieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                    )
                )
                .toList();
        }
    }
}


```
</div>
</details>


### 2️⃣ 사용자 로그인 엔드 포인트
- `POST https://choizz-onboarding.p-e.kr/users/login`

#### (1) 로그인 (JWT 사용)
- Spring Security를 사용하여 로그인 기능을 구현하였습니다.
    - Spring Security는 인증과 보안을 위한 많은 설정 등을 제공합니다.
    
- email(username)과 password에 인증에 성공하면 Access Token과 Refresh Token을 Response 헤더에 발급합니다.
- 만약 Access Token의 유효기간이 지난 다면, Request Header에 Refresh 토큰을 넣어 요청하면, Refresh 토큰 검증 후 Access Token을 재발급 합니다.

#### (2) 로그인 인증 실패(email, password 오류)
- 만약 email과, password가 일치하지 않는다면, UsernameNotFoundException 예외를 던집니다.

#### (3) email, password 유효성 검증
- Spring Security는 `servlet filter 단에서 인증 검사`를 합니다. 반면, Spring Validation은 WAS 안에서 실행됩니다.
- 그래서, email 형식이나, password 형식이 올바르지 않다면, filter 영역에서 email과 password를 검증하게 되고
저장되어 있는 유저 정보와 당연히 다를 것이기 때문에 인증 실패 예외를 던지게 됩니다.
```json
//로그인시 email과 password 유효성 검증 실패
HTTP/1.1 401 Unauthorized
{
    "time": "2023-08-08T05:49:05.43275",
    "data": {
        "status": 401,
        "msg": "자격 증명에 실패하였습니다." or "Bad credentials"
    }
}
```



### 3️⃣ 새로운 게시글을 생성하는 엔드포인트
- `POST https://choizz-onboarding.p-e.kr/posts?userId={userId}`

#### (1) 포스팅 생성
-  PostController에서 Request body의 title과 content를 받습니다.
-  PostService에서 포스팅을 저장하고, Post 객체와 User 간의 연관관계를 맺어 관리합니다.

> User 객체와 Post 객체는 1:N 관계를 맺습니다.

### 4️⃣ 게시글 목록을 조회하는 엔드포인트
- `GET http://localhost:8081/posts?page={page}&size={size}&sort={sort}"`

#### (1) 게시글 목록 조회
 - 게시글 목록을 DB로 부터 조회해 오는 로직은 `QueryDsl`을 사용했습니다.
    - 페이지네이션 로직을 구현하는 객체를 만들고 그 로직을 적용하기에 QueryDsl을 사용하는 것이 적합하다고 판단했습니다.   
        - ex) size의 최소 값은 10입니다. 즉, size가 10 이하로 들어온다고 하더라도 10으로 설정하는 로직이 필요합니다.
    - Pageable 객체는 테이블의 count를 세는 query가 추가되는데, 현재로선, count를 세는 쿼리를 날릴 필요가 없다고 생각해 QueryDsl을 통해 직접 페이지네이션했습니다.

<details>
<summary> # QueryDsl 페이지네이션</summary>
<div markdown="1">

```java
@RequiredArgsConstructor
public class PostRepositoryImpl implements QueryPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findLists(final PostSearching postSearching) {
        return jpaQueryFactory.selectFrom(post)
            .limit(postSearching.getSize())
            .offset(postSearching.getOffset())
            .orderBy(getSort(postSearching))
            .fetch();
    }

    private OrderSpecifier<Long> getSort(final PostSearching postSearching) {
        return postSearching.getSort() == Sort.DESC ? post.id.desc() : post.id.asc();
    }
}
```
</div>
</details>

> 포스팅을 생성, 수정, 삭제하는 역할을 하는 PostService와,
> 조회만 하는 QueryPostService를 구별했습니다.
    - @Transcation과 @Transcation과(readOnly = true)를 분리를 의도했습니다.
### 5️⃣ 특정 게시글을 조회하는 엔드포인트
- `GET http://localhost:8081/posts/{postId}"`

#### (1) 특정 게시글 조회
- postId를 path에 넣으면 해당 postId를 가진 포스팅을 조회합니다.



### 6️⃣ 특정 게시글을 수정하는 엔드포인트
- `PATCH http://localhost:8081/posts/{postId}?userId={userId}"`

#### (1) 작성자만이 포스팅 수정
- PostController에서 Request body의 수정할 title과 content를 받습니다. 
- PostService에서 Post 객체가 가지고 참조하고 있는 User 객체를 통해 userId 일치 여부를 확인한 후 게시글을 수정합니다.
```java
 private void verifyOwner(final Long userId, final Post post) {
    if (isNotOwner(userId, post)) {
        throw new BusinessLoginException(ExceptionCode.NOT_MATCHING_OWNER);
    }
}
```
- 만약 작성자가 아닌 타인이 포스팅 수정 시도 시 NOT_MATCHING_OWNER 예외를 던집니다.


### 7️⃣ 특정 게시글을 삭제하는 엔드포인트
`DELETE http://localhost:8081/posts/{postId}?userId={userId}`

#### (1) 작성자만이 포스팅을 삭제
- 작성자만이 포스팅을 삭제할 수 있습니다.
- 만약 작성자가 아닌 타인이 포스팅 삭제 시도 시 NOT_MATCHING_OWNER 예외를 던집니다.

---
### 📌 Test Code
 `Junit5와 RestAssured`를 사용하여 약 50개의 통합 테스트 및 인수 테스트를 작성했습니다.
- Repository 레이어와 Service 레이어를 통합하여 테스트를 했습니다.
- 인수 테스트의 경우 BDD 스타일을 사용하는 RestAssured가 가독성이 좋다고 판단해 선택했습니다.

![](https://github.com/choizz156/choizz156/blob/60ce9c63985b8db12a4d3dbbf67cd454689db855/imagesFile/test.png)

---

## 📖 API Reference

#### Spring Rest Docs 사용
- Spring Rest Docs는 테스트를 통과해야 문서가 작성되므로 신뢰성이 보장됩니다.
- Swagger와 달리, 프로덕션 코드에 침투적이지 않습니다.
    
    - [API 문서](https://choizz-onboarding.p-e.kr)

