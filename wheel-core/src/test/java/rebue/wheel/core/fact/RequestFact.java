package rebue.wheel.core.fact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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
