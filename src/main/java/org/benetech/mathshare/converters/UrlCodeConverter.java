package org.benetech.mathshare.converters;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Locale;

//PMD is marking cleanTheCode and cleanTheCode methods as unused (but both are used)
@SuppressWarnings("PMD.UnusedPrivateMethod")
@SuppressFBWarnings(value = "SLF4J_FORMAT_SHOULD_BE_CONST",
        justification = "We need Logger to contain not-constant data to be valuable")
public abstract class UrlCodeConverter {

    public static String toUrlCode(long number) {
        try {
            byte[] bytes = longToByteArray(number);
            return cleanTheCode(new Base32().encodeToString(bytes));
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
            return null;
        }
    }

    public static long fromUrlCode(String code) {
        validateCode(code);
        try {
            return convertByteArrayToLong(new Base32().decode(code));
        } catch (BufferOverflowException | BufferUnderflowException e) {
            getLogger().error(e.getMessage(), e);
        }
        throw new IllegalArgumentException("Code is probably too long");
    }

    private static void validateCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code can't be null");
        } else if (code.isEmpty()) {
            throw new IllegalArgumentException("Code can't be empty");
        } else if (!code.matches("[a-zA-Z2-7]*")) {
            throw new IllegalArgumentException("Code is not valid");
        }
    }

    private static String cleanTheCode(String code) {
        return code.replace("=", "").toUpperCase(Locale.ROOT);
    }

    private static byte[] longToByteArray(long number) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeLong(number);
        dos.flush();
        return bos.toByteArray();
    }

    private static long convertByteArrayToLong(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        return byteBuffer.getLong();
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(UrlCodeConverter.class);
    }

    private UrlCodeConverter() {
    }
}
