package org.hisp.dhis.asha.idgen;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Mithilesh Kumar Thakur
 */
public class IdentifierGenerator
{
    /**
     * Creates a new Patient Identifier: (BirthDate)(Gender)(XXXXXX)(checkdigit) <br>
     * <strong>BirthDate</strong> = YYYYMMDD <br>
     * <strong>Gender</strong> = Male : 1 | Female : 0<br>
     * <strong>XXXXXX</strong> = random digits e.g. from 0 - 999999 <br>
     * <strong>checkdigit</strong>= using the Lunh Algorithm
     * 
     * @param birthDate
     * @param gender
     * @return
     */
    public static String getNewIdentifier( Date birthDate, String gender )
    {
        String noCheck = formatDate( birthDate ) + formatGender( gender )
            + getFixLengthOfNumber( new Random().nextInt( 100000 ), 6 );
        return noCheck + getCheckdigit( noCheck );
    }

    /**
     * Using the Luhn Algorithm to generate check digits
     * 
     * @param idWithoutCheckdigit
     * @return idWithCheckdigit
     */
    private static int getCheckdigit( String idWithoutCheckdigit )
    {
        idWithoutCheckdigit = idWithoutCheckdigit.trim().toUpperCase();
        int sum = 0;
        for ( int i = 0; i < idWithoutCheckdigit.length(); i++ )
        {
            char ch = idWithoutCheckdigit.charAt( idWithoutCheckdigit.length() - i - 1 );

            int digit = (int) ch - 48;
            int weight;
            if ( i % 2 == 0 )
            {
                weight = (2 * digit) - (int) (digit / 5) * 9;
            }
            else
            {
                weight = digit;
            }
            sum += weight;
        }
        sum = Math.abs( sum ) + 10;
        return (10 - (sum % 10)) % 10;
    }

    public static boolean isValidCC( String num )
    {
        final int[][] sumTable = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 } };
        int sum = 0, flip = 0;

        for ( int i = num.length() - 1; i >= 0; i--, flip++ )
            sum += sumTable[flip & 0x1][num.charAt( i ) - '0'];
        return sum % 10 == 0;
    }

    private static String getFixLengthOfNumber( long number, int length )
    {
        int i = 0;
        String pattern = "";
        if ( length == 0 )
            pattern = "000000";
        while ( i < length && length > 0 )
        {
            pattern += "0";
            i++;
        }
        DecimalFormat df = new DecimalFormat( pattern );
        return df.format( number );
    }

    private static String formatDate( Date birthDate )
    {
        SimpleDateFormat formater = new SimpleDateFormat( "yyyyMMdd" );
        String bd = formater.format( birthDate );
        return bd;
    }

    private static String formatGender( String gender )
    {
        return gender.equalsIgnoreCase( "f" ) ? "0" : "1";
    }
}
