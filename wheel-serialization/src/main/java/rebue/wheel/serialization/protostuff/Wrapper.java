package rebue.wheel.serialization.protostuff;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Wrapper<T> {
    private T inner;
}
