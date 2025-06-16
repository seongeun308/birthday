package kim.birthday.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class PublicIdGenerator {
    public static String generatePublicId() {
        return NanoIdUtils.randomNanoId();
    }
}
