package inf.awieclawski.sniffer.utls;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {

    public static Object getFieldValue(Object obj, String fieldName) throws IllegalAccessException {
        return FieldUtils.readField(obj, fieldName, true);
    }

}
