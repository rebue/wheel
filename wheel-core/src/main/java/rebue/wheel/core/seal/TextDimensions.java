package rebue.wheel.core.seal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextDimensions {
    private int width;
    private int height;
    private int ascent;
    private int descent;
    private int leading;
}
