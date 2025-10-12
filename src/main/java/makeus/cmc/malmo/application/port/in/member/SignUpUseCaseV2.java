package makeus.cmc.malmo.application.port.in.member;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * V2 회원가입 UseCase
 * - 개인의 startLoveDate는 더 이상 입력받지 않습니다.
 * - 커플 연동 후 별도로 연애 시작일을 설정합니다.
 */
public interface SignUpUseCaseV2 {

    void signUp(SignUpCommand command);

    @Data
    @Builder
    class SignUpCommand {
        private Long memberId;
        private List<TermsCommand> terms;
        private String nickname;
        private Long loveTypeId;
    }

    @Data
    @Builder
    class TermsCommand {
        private Long termsId;
        private Boolean isAgreed;
    }
}
