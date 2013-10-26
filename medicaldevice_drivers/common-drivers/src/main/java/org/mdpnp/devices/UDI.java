package org.mdpnp.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ice.DeviceIdentity;

public class UDI {
    private UDI() {

    }
    private static final Logger log = LoggerFactory.getLogger(UDI.class);
    private static final int UDI_LENGTH = 36;
    private static final char[] UDI_CHARS = new char[26*2+10];
    static {
        int x = 0;
        for(char i = 'A'; i <= 'Z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for(char i = 'a'; i <= 'z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for(char i = '0'; i <= '9'; i++) {
            UDI_CHARS[x++] = i;
        }
    }
    public static String randomUDI() {
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random(System.currentTimeMillis());
        for(int i = 0; i < UDI_LENGTH; i++) {
            sb.append(UDI_CHARS[random.nextInt(UDI_CHARS.length)]);
        }
        return sb.toString();
    }

    public static void randomUDI(DeviceIdentity di) {
        di.unique_device_identifier = randomUDI();
        log.debug("Created Random UDI:"+di.unique_device_identifier);
    }
}
