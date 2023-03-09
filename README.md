# OAuth2.0-jwt-sample

### 설명
SpringBoot에서 Spring Security와 JWT를 사용하여 소셜 로그인(OAuth2)를 구현한 예제 프로젝트입니다. 특징으로는 로그인의 백엔드 처리 프로세스를 REST API 방식으로 구현하였습니다. 소셜 로그인은 Google과 Naver 2개를 연결하였습니다.

### 작동
* 소셜 로그인 페이지로 Redirect 요청("/login/oauth2/authorization/{socialType}")
  * 사용자가 웹사이트의 로그인 화면에서 특정한 소셜 로그인 버튼을 클릭하면 이 요청을 처리합니다.
  * socialType에 따라 각 소셜 로그인 페이지로 리다이렉트 해줍니다.
  * socialType
    * GOOGLE
    * NAVER
  
* 소셜 로그인 이후 1회용 code를 통한 로그인 처리("/login/oauth2/code/{socialType}")
  * 이 요청의 URL은 각 소셜 로그인 설정에 적용하는 rediect url입니다.
  * 소셜 로그인 페이지에서 로그인 승인이 되면 1회용 코드(Access Code)를 받게 되는데, 이 코드를 이용하여 api 서버로부터 access token과 refresh token을 받게 됩니다.
