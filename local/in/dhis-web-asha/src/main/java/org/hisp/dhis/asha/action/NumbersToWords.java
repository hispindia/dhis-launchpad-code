package org.hisp.dhis.asha.action;

import java.util.Scanner;

/**
 * @author Mithilesh Kumar Thakur
 */
public class NumbersToWords {
    //array containing single digits
    public static final String[] digits = 
    {
        "",
        " one",
        " two",
        " three",
        " four",
        " five",
        " six",
        " seven",
        " eight",
        " nine",
        
    };
    
    //array containing teens
    public static final String[] teens = 
    {
        " eleven",
        " twelve",
        " thirteen",
        " fourteen",
        " fifteen",
        " sixteen",
        " seventeen",
        " eighteen",
        " nineteen"
    };
    
    //array containing tens numbers
    public static final String[] tens = 
    {
        "ten",
        "twenty",
        "therty",
        "fourty",
        "fifty",
        "sixty",
        "eighty",
        "ninety",
    };
    
    //string for hundreds numbers
    public static final String hundred = "hundred";
    
    
    
    public static int tmpNum,  //temp number for calculations
                      newNum,  //number reslut from division
                      number,  //number to be converted to words
                      result;  //result number
                                                

    public static void main(String[] args) {
        
        
        Scanner in = new Scanner(System.in);
        
        //getting user input
        System.out.print("Please enter a number: ");
        number = in.nextInt(); //number to be converted
        //input validation
        while(number <= 0)
        {
            System.out.println("Number should be biger than cero!");
            System.out.print("Please enter a number:");
            number = in.nextInt();
        }
        
       while(number != 0)
       {
           //converting num to word for num smaller than 10
           if(number > 0 && number < 10)
           {
               newNum = number / 10;
               tmpNum = newNum * 10;
               result = number - tmpNum;
               System.out.println("digits " + digits[result+1]);
               number = newNum;
           }
           //converting num to word for num < 10 && > 19
           if(number > 10 && number < 19)
           {
               newNum = number / 10;
               tmpNum = newNum * 10;
               result = number - tmpNum;
               System.out.println("teens " + teens[result]);
               number = newNum;
           }
           //converting for num tens digits
           if(number == 10 || (number > 19 && number < 100))
           {
               newNum = number / 10;
               tmpNum = newNum * 10;
               result = number - tmpNum;
               System.out.println("tens " + tens[result]);
               number = newNum;
               
           }
           //converting for hundreds
           if(number > 100 && number < 999)
           {
               newNum = number / 10;
               tmpNum = newNum * 10;
               result = number - tmpNum;
               System.out.println("hundreds " + hundred);
               number = newNum;
           }
       }
              
    }
}
