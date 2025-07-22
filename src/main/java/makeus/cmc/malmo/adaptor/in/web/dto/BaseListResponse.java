package makeus.cmc.malmo.adaptor.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.MDC;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseListResponse<T> {
    private int size;
    private Integer page;
    private List<T> list;
    private Long totalCount;

    public static <T> BaseResponse<BaseListResponse<T>> success(List<T> data, int page, Long totalCount) {
        return new BaseResponse<>(
                MDC.get("request_id"),
                true,
                "요청이 성공적으로 처리되었습니다.",
                new BaseListResponse<>(data.size(), page, data, totalCount)
        );
    }

    public static <T> BaseResponse<BaseListResponse<T>> success(List<T> data, Long totalCount) {
        return new BaseResponse<>(
                MDC.get("request_id"),
                true,
                "요청이 성공적으로 처리되었습니다.",
                new BaseListResponse<>(data.size(), null, data, totalCount)
        );
    }
}
