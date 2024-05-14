package rebue.wheel.core.fact;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestFact {
    /**
     * 请求的URI
     */
    private String              uri;
    /**
     * 请求的body
     */
    private Map<String, String> body;
}
