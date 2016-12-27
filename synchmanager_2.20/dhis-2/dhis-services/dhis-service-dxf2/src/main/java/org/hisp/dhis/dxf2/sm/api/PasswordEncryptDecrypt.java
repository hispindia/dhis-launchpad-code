package org.hisp.dhis.dxf2.sm.api;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author Mithilesh Kumar Thakur
 */
public class PasswordEncryptDecrypt
{

    private static final String ALGORITHM = "AES";

    private static final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e',
        't', 'K', 'e', 'y' };

    public String encrypt( String valueToEnc )
        throws Exception
    {
        Key key = generateKey();
        Cipher c = Cipher.getInstance( ALGORITHM );
        c.init( Cipher.ENCRYPT_MODE, key );
        byte[] encValue = c.doFinal( valueToEnc.getBytes() );
        String encryptedValue = new BASE64Encoder().encode( encValue );
        return encryptedValue;
    }

    public String decrypt( String encryptedValue )
        throws Exception
    {
        Key key = generateKey();
        Cipher c = Cipher.getInstance( ALGORITHM );
        c.init( Cipher.DECRYPT_MODE, key );
        byte[] decordedValue = new BASE64Decoder().decodeBuffer( encryptedValue );
        byte[] decValue = c.doFinal( decordedValue );
        String decryptedValue = new String( decValue );
        return decryptedValue;
    }

    private static Key generateKey()
        throws Exception
    {
        Key key = new SecretKeySpec( keyValue, ALGORITHM );
        return key;
    }

}
