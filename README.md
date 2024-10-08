# study-together / 회원가입 구현 

# Study-Together 



## 기능 목록
- **회원가입**: 이메일, 아이디, 비밀번호, 닉네임을 입력하여 회원가입을 진행
- **로그인**: 로그인 후 엑세스 토큰과 리프레시 토큰을 발급
- **인증**: 엑세스 토큰을 통해 인증을 수행하며, 필요 시 리프레시 토큰을 통해 엑세스 토큰을 갱신
- **저장소** : 엑세스 토큰  -> body
             리프레시 토큰 -> 쿠키에 발급
## 테스트 과정
### Postman으로 로그인 및 토큰 확인
- **POST** 요청으로 `/login` 엔드포인트에서 로그인 요청을 확인합니다.
    - Content-Type: `application/x-www-form-urlencoded`
    - Body:
        - `username`: 사용자 아이디
        - `password`: 사용자 비밀번호
- 응답 헤더에서 `Authorization: Bearer <토큰>` 확인
- Workbench에 유저데이터 확인

## 체크리스트
- [x] JWT 토큰 생성
- [x] 엑세스 토큰 발급 확인
- [x] 리프레시 토큰 발급 확인
- [x] 토큰 갱신 로직 구현
