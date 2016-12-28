package org.hisp.dhis.dxf2.sm.api;

import org.hisp.dhis.common.BaseIdentifiableObject;

/**
 * @author BHARATH
 */
public class SynchInstance
    extends BaseIdentifiableObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    //private static final String ALGORITHM = "AES";

    //private static final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

    public static final String SYNCH_TYPE_META_DATA = "meta-data";

    public static final String SYNCH_TYPE_DATA = "data";

    private String url;

    private String userId;

    private String password;

    private String synchType;
    
    private String type;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SynchInstance()
    {
    }

    public SynchInstance( String name, String url, String userId, String password, String synchType )
    {
        this.name = name;
        this.url = url;
        this.userId = userId;
        this.password = password;
        this.synchType = synchType;
    }
    
    public SynchInstance( String name, String url, String userId, String password, String synchType, String type )
    {
        this.name = name;
        this.url = url;
        this.userId = userId;
        this.password = password;
        this.synchType = synchType;
        this.type = type;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public String getPassword()
    {
        /*
         * String decodePassword = null;
         * 
         * try { decodePassword = decrypt( password ); } catch ( Exception e ) {
         * e.printStackTrace(); }
         * 
         * System.out.println( "Decode Password is" + " : " + decodePassword );
         * return decodePassword;
         */
        return password;
    }

    public void setPassword( String password )
    {
        /*
         * String enCodePassword = null; try { enCodePassword = encrypt(
         * password ); } catch ( Exception e ) { e.printStackTrace(); }
         * 
         * System.out.println( "Incode Password is" + " : " + enCodePassword );
         */
        this.password = password;
    }

    public String getSynchType()
    {
        return synchType;
    }

    public void setSynchType( String synchType )
    {
        this.synchType = synchType;
    }
    
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }
    
    
    // get Decrypt Password
    public String getDecryptPassword( String enCodePassword )
    {
          String decodePassword = null;
          
          try 
          { 
              SecretKeyGenerator secretKeyGenerator = new SecretKeyGenerator();
              decodePassword = secretKeyGenerator.decrypt( enCodePassword ); 
          } 
          catch ( Exception e ) 
          {
              e.printStackTrace(); 
          }
          
          //System.out.println( "Decode Password is" + " : " + decodePassword );
          return decodePassword;
    }
    
    
    // methods for encrypt and decrypt
    /*
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
    */
    
}
