package makeus.cmc.malmo.adaptor.out.oauth;

import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.exception.OAuthUnlinkFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KakaoUnlinkAdapter {

    @Value("${kakao.oauth.admin-key}")
    private String kakaoAdminKey;

    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v1/user/unlink";

    public void unlink(Long providerId) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        // 2. 요청 본문(body) 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", String.valueOf(providerId));

        // 3. 헤더와 본문을 포함하는 HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // 4. RestTemplate을 사용하여 POST 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // 성공적으로 응답을 받았을 때의 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Kakao unlink success: {}", response.getBody());
            }

        } catch (HttpClientErrorException e) {
            // 카카오 API로부터 에러 응답(4xx)을 받았을 때의 처리
            log.error("Kakao unlink failed: {}", e.getResponseBodyAsString());
            throw new OAuthUnlinkFailureException("Failed to unlink user from Kakao.", e);
        }
    }

}
