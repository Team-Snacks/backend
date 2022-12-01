# Snacks Backend Server
README Ver 1.0 
## Technology Stack
| Component	| Technology |
|-----------|-----------|
| Language | Java 17 |
| Backend | SpringBoot|
| Security | Token Based (Spring Security and JWT) |
| Persistence | JPA (Using Spring Data) |
| REST Documentation | Swagger Hub |
| Server Build Tools | Gradle |
| DBMS | MariaDB |
| In Memory DB	| Redis |

## Directory Structure
```yaml
PROJECT_FOLDER
|
└──[src]      
│  └──[main/java/com/snacks/backend]     
|     |  BackendApplication.java   
│     └──[config]             # Configuration 파일들이 위치한 디렉토리.
|     |  SecurityConfig.java  # Spring Security Filter에 관한 설정이 들어있는 파일.
│     └──[controller]         # Controller 파일들이 위치한 디렉토리.
|     |  AuthController.java  # 회원가입(/auth)과 리프레시 토큰 발급(/auth/refresh) API가 위치한 Controller.
│     └──[dto]                # 클라이언트와 주고받는 dto 파일들이 위치한 디렉토리.
│     └──[jwt]                # jwt 토큰 인증에 필요한 파일들을 모아놓은 디렉토리.
|        |  JwtAuthenticationFilter.java       # 사용자의 ID와 Password를 받아 로그인을 수행하고 jwt토큰을 반환하는 파일.
|        |  JwtAuthorizationFilter.java        # 인증이 필요한 API 요청 시, 사용자의 요청에서 토큰을 가져와 인증을 수행하는 파일.
│        └──[auth]
|           |   CustomUserDetails.java         # UserDetails 인터페이스를 상속받은 클래스, 사용자의 인증 정보 저장에 사용된다.
|           └── CustomUserDetailsService.java  # UserDetailsService 인터페이스를 상속받은 클래스,
|     |                                        # JwtAuthenticationFilter에서 넘겨준 사용자의 ID와 Password를 이용하여 DB에서 이를 조회하는 역할을 한다.
|     |
│     └──[redis]              # redis관련 파일들이 위치한 디렉토리.
|     |   RedisService.java   # redis에 값을 넣고, 가져오는 일을 수행하는 Service 파일.      
│     └──[repository]         # repository 파일들이 위치한 디렉토리.
│     └──[response]           # 클라이언트에게 반환할 Response Body의 내용에 넣을 데이터 양식을 관리하는 파일들이 위치한 디렉토리. 
│     └──[service]            # service 파일들이 위치한 디렉토리.
|   |
|   └──[test/java/com/snacks/backend]          # 테스트 파일들이 있는 디렉토리.
|
|   .gitignore
|   build.gradle
|   gradlew
|   gradlew.bat
└── settings.gradle
```