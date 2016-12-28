package org.hisp.dhis.dxf2.sm.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SecretKeyGenerator 
{
    
    private static String algorithm;
    
    // In properties file length  of skey.keyValue is 16
    private static byte[] keyValue;

    static Properties properties;
    
    // constructor
    /*****this constructor takes the algorithm , keyValue  from a file a file**********/
    public SecretKeyGenerator() throws FileNotFoundException, IOException
    {
        properties = new Properties();

        properties.load( new FileReader( System.getenv( "DHIS2_HOME" ) + File.separator + "hibernate.properties" ) );
        
        algorithm = getAlgorithm();
        keyValue = getKeyValue();
    }
    
    public SecretKeyGenerator( String algorithm, byte[] keyValue )
    {
        SecretKeyGenerator.algorithm = algorithm;
        SecretKeyGenerator.keyValue = keyValue;
        
    }    
    
    // getter

    public static String getAlgorithm()
    {
        return properties.getProperty( "skey.algorithm" );
    }

    public static byte[] getKeyValue()
    {
        return properties.getProperty( "skey.keyValue" ).getBytes();
    }

    // methods for encrypt and decrypt
    public String encrypt( String valueToEnc )
        throws Exception
    {
        Key key = generateKey();
        Cipher c = Cipher.getInstance( algorithm );
        c.init( Cipher.ENCRYPT_MODE, key );
        byte[] encValue = c.doFinal( valueToEnc.getBytes() );
        String encryptedValue = new BASE64Encoder().encode( encValue );
        return encryptedValue;
    }


    public String decrypt( String encryptedValue )
        throws Exception
    {
        Key key = generateKey();
        Cipher c = Cipher.getInstance( algorithm );
        c.init( Cipher.DECRYPT_MODE, key );
        byte[] decordedValue = new BASE64Decoder().decodeBuffer( encryptedValue );
        byte[] decValue = c.doFinal( decordedValue );
        String decryptedValue = new String( decValue );
        return decryptedValue;
    }

    private static Key generateKey()
        throws Exception
    {
        //System.out.println( "Algorithm : "  + algorithm + " KeyValue : " + keyValue );
        
        Key key = new SecretKeySpec( keyValue, algorithm );
        
        //System.out.println( "key : "  + key.toString().length() );
        
        return key;
    }    
    
}
