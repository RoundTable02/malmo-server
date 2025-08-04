package makeus.cmc.malmo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JosaUtils 클래스 테스트")
public class JosaUtilsTest {

    @Test
    @DisplayName("한글 닉네임 - 받침 있음")
    void testHangulNicknameWithBatchim() {
        assertEquals("하늘아", JosaUtils.아야("하늘"));
        assertEquals("밥아", JosaUtils.아야("밥"));
    }

    @Test
    @DisplayName("한글 닉네임 - 받침 없음")
    void testHangulNicknameWithoutBatchim() {
        assertEquals("철수야", JosaUtils.아야("철수"));
        assertEquals("지우야", JosaUtils.아야("지우"));
    }

    @Test
    @DisplayName("영어 닉네임 - 받침 있음으로 추정")
    void testEnglishNicknameWithBatchimLike() {
        assertEquals("mac아", JosaUtils.아야("mac"));
        assertEquals("jack아", JosaUtils.아야("jack"));
        assertEquals("club아", JosaUtils.아야("club"));
    }

    @Test
    @DisplayName("영어 닉네임 - 받침 없음으로 추정")
    void testEnglishNicknameWithoutBatchimLike() {
        assertEquals("case야", JosaUtils.아야("case"));
        assertEquals("jenny야", JosaUtils.아야("jenny"));
        assertEquals("neo야", JosaUtils.아야("neo"));
    }

    @Test
    @DisplayName("숫자 닉네임")
    void testNumberNickname() {
        assertEquals("123아", JosaUtils.아야("123"));
        assertEquals("1아", JosaUtils.아야("1"));
        assertEquals("2야", JosaUtils.아야("2"));
        assertEquals("3아", JosaUtils.아야("3"));
        assertEquals("4야", JosaUtils.아야("4"));
        assertEquals("5야", JosaUtils.아야("5"));
        assertEquals("6아", JosaUtils.아야("6"));
        assertEquals("7아", JosaUtils.아야("7"));
        assertEquals("8아", JosaUtils.아야("8"));
        assertEquals("9야", JosaUtils.아야("9"));
        assertEquals("0아", JosaUtils.아야("0"));
    }

    @Test
    @DisplayName("한글+숫자, 영어+숫자 혼합 닉네임")
    void testMixedNickname() {
        assertEquals("철수1아", JosaUtils.아야("철수1"));
        assertEquals("mac2야", JosaUtils.아야("mac2"));
        assertEquals("neo7아", JosaUtils.아야("neo7"));
    }

    @Test
    @DisplayName("빈 문자열, null 처리")
    void testNullOrEmpty() {
        assertEquals("", JosaUtils.아야(null));
        assertEquals("", JosaUtils.아야(""));
    }
}
