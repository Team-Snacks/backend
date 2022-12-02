package com.snacks.backend.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacks.backend.dto.UserDto;
import com.snacks.backend.entity.User;
import com.snacks.backend.jwt.JwtAuthenticationFilter;
import com.snacks.backend.jwt.JwtProvider;
import com.snacks.backend.redis.RedisService;
import com.snacks.backend.repository.AuthRepository;
import com.snacks.backend.response.ResponseService;
import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final AuthRepository authRepository;
  private final HttpSession httpSession;

  @Autowired
  JwtProvider jwtProvider;

  @Autowired
  RedisService redisService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

//    System.out.println(registrationId);
//    for (Entry<String, Object> x:
//    oAuth2User.getAttributes().entrySet()) {
//      System.out.println("@ " + x);
//    }

    User user = saveOrUpdate(attributes);
//    httpSession.setAttribute("user", new SessionUser(user));


    /*
    String refreshToken = jwtProvider.createToken(attributes.getEmail(), "refresh");
    String accessToken = jwtProvider.createToken(attributes.getEmail(), "access");

    redisService.setValues(attributes.getEmail(), refreshToken);

    //헤더에 값은 어떻게 넣어주지.....
    //반환은 어떻게 해주지....
    //response.addHeader("Authorization",
    //    "Bearer " + accessToken);

    ResponseService responseService = new ResponseService();
    ObjectMapper mapper = new ObjectMapper();
    /*try {
      mapper.writeValue(response.getWriter(),
          responseService.getTokenResponse("Bearer " + refreshToken,
              "Bearer " + accessToken));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }*/

    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
        attributes.getAttributes(),
        attributes.getNameAttributeKey()
    );
  }

  private User saveOrUpdate(OAuthAttributes attributes){
    User user = authRepository.findByEmail(attributes.getEmail())
        .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
        .orElse(attributes.toEntity());
    return authRepository.save(user);
  }

}


