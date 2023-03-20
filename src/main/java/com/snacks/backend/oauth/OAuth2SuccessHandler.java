package com.snacks.backend.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.backend.jwt.JwtProvider;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.response.ResponseService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtProvider jwtProvider;
  private final RedisService redisService;

  /**
   * 구글 로그인 성공 시 access token, refresh 토큰 후 반환
   * @param request        the request which caused the successful authentication
   * @param response       the response
   * @param authentication the <tt>Authentication</tt> object which was created during
   *                       the authentication process.
   * @throws IOException
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
    String email = defaultOAuth2User.getAttributes().get("email").toString();

    String refreshToken = jwtProvider.createToken(email, "refresh", "google");
    String accessToken = jwtProvider.createToken(email, "access", "google");

    redisService.setValues(authentication.getName(), refreshToken);

    response.addHeader("Authorization",
        "Bearer " + accessToken);

    ResponseService responseService = new ResponseService();
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(response.getWriter(),
          responseService.getTokenResponse("Bearer " + refreshToken,
              "Bearer " + accessToken));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
