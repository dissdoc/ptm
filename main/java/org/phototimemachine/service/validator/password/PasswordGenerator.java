package org.phototimemachine.service.validator.password;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;

public class PasswordGenerator {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private MessageDigest algorithm;

    private static final String charset = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public PasswordGenerator() {

    }

    public PasswordGenerator(String firstName, String lastName, String email, String password) {
        try {
            this.firstName = "firstName";
            this.lastName = "lastName";
            this.email = email;
            this.password = password;

            algorithm = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getPassword() {
        String esi = getHexString(password);
        String value = getHexString(firstName, lastName, email);
        byte[] bytes = algorithm.digest(String.format("%s#%s", value, esi).getBytes());
        return toHexBytes(bytes);
    }

    private String toHexBytes(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b: hash)
            formatter.format("%02x", b);
        return formatter.toString();
    }

    private String getHexString(String... args) {
        StringBuilder sb = new StringBuilder();
        for (String item: args)
            sb.append(item);
        return String.format("%040x", new BigInteger(sb.toString().getBytes()));
    }

    public String generatePassword(int length) {
        Random rand = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= length; i++ ) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }
}
