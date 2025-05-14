package com.sipa.boot.java8.common.utils;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhouxiajie
 * @date 2019-02-14
 */
public class EtlUtils {
    private static final String RIGHT_HEADER = "##";

    private static final String RIGHT_HEADER_HEX = "2323";

    public static boolean isValidData(String data) {
        return StringUtils.isNotBlank(data) && (isHex(data) || isAscii(data));
    }

    public static boolean isHex(String data) {
        return data.startsWith(RIGHT_HEADER_HEX);
    }

    public static boolean isAscii(String data) {
        return data.startsWith(RIGHT_HEADER);
    }

    public static String hex2Ascii(String hex) {
        try {
            return new String(Hex.decodeHex(hex));
        } catch (DecoderException e) {
            return null;
        }
    }

    public static String ascii2Hex(String ascii) {
        return new String(Hex.encodeHex(ascii.getBytes(), false));
    }

    public static byte[] getTimeBinary() {
        LocalDateTime lTime = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        String localTime = df.format(lTime);
        String year0x = localTime.substring(2, 4);
        String month0x = localTime.substring(4, 6);
        String day0x = localTime.substring(6, 8);
        String hours0x = localTime.substring(9, 11);
        String min0x = localTime.substring(12, 14);
        String seconds0x = localTime.substring(15, 17);
        return new byte[] {Byte.parseByte(year0x), Byte.parseByte(month0x), Byte.parseByte(day0x),
            Byte.parseByte(hours0x), Byte.parseByte(min0x), Byte.parseByte(seconds0x)};
    }

    /**
     * 将两位的16进制Hex 转成8位的 2进制 1、0
     */
    public static String twoBytesToEightBinaryString(String air0x) {
        Integer airIn = Integer.parseInt(air0x, 16);
        String dd = Integer.toBinaryString(airIn);
        Integer ii = Integer.valueOf(dd);
        return String.format("%08d", ii);
    }

    /**
     * Convert byte[] to int
     *
     * @param b
     *            byte array
     * @param length
     *            byte length
     * @return value of conversion
     */
    public static int byteArrayToInt(byte[] b, int length) {
        if (SipaBootCommonConstants.Number.INT_2 == length) {
            return b[1] & 0xFF | (b[0] & 0xFF) << 8;
        } else if (SipaBootCommonConstants.Number.INT_4 == length) {
            return b[3] & 0xFF | (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8;
        } else if (SipaBootCommonConstants.Number.INT_3 == length) {
            return b[2] & 0xFF | (b[0] & 0xFF) << 16 | (b[1] & 0xFF) << 8;
        }
        return 0;
    }
}
