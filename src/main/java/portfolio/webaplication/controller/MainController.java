package portfolio.webaplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import portfolio.webaplication.dto.MemberDTO;
import portfolio.webaplication.model.KakaoProfile;
import portfolio.webaplication.model.OAuthToken;
import portfolio.webaplication.service.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {

    private final MemberService memberService;

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        log.info("MemberDTO={}", memberDTO);
        memberService.save(memberDTO);
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    //    @GetMapping("/kakao/callback")
//    public @ResponseBody String kakaoCallbakc(String code) {
//        RestTemplate rt = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", "05069a23f2a3b2bf5fb06484c8fd62f9");
//        params.add("redirect_uri", "http://localhost:8080/main/kakao/callback");
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//
//        ResponseEntity<String> response = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        OAuthToken oAuthToken = null;
//        try {
//            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        System.out.println("카카오 엑세스 토큰 : " + oAuthToken.getAccess_token());
//
//        RestTemplate rt2 = new RestTemplate();
//        HttpHeaders headers2 = new HttpHeaders();
//        headers2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
//        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);
//
//        ResponseEntity<String> response2 = rt2.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.POST,
//                kakaoProfileRequest,
//                String.class
//        );
//
//        ObjectMapper objectMapper2 = new ObjectMapper();
//        KakaoProfile kakaoProfile = null;
//        try {
//            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(kakaoProfile.getId());
//        System.out.println(kakaoProfile.getKakao_account().getEmail());
//        return response2.getBody();
//    }
    @GetMapping("/kakao/callback")
    public @ResponseBody String kakaoCallbackGet(@RequestParam("code") String code) {
        OAuthToken oAuthToken = getKakaoAccessToken(code);
        System.out.println("카카오 엑세스 토큰 : " + oAuthToken.getAccess_token());

        return getKakaoUserProfile(oAuthToken.getAccess_token());
    }

    @PostMapping("/kakao/callback")
    public @ResponseBody String kakaoCallbackPost(@RequestParam("code") String code) {
        OAuthToken oAuthToken = getKakaoAccessToken(code);
        System.out.println("카카오 엑세스 토큰 : " + oAuthToken.getAccess_token());

        return getKakaoUserProfile(oAuthToken.getAccess_token());
    }

    @PostMapping("/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            session.setAttribute("loginEmail", loginResult.getMemberEmail());
            return "main";
        } else {
            return "login";
        }
    }

    private OAuthToken getKakaoAccessToken(String code) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "05069a23f2a3b2bf5fb06484c8fd62f9");
        params.add("redirect_uri", "http://localhost:8080/main/kakao/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oAuthToken;
    }

    private String getKakaoUserProfile(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println(kakaoProfile.getId());
        System.out.println(kakaoProfile.getKakao_account().getEmail());

        return response.getBody();
    }
}
