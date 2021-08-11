package ru.readme.chatapp.util;

import android.os.Build;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class CheckNick {
    private static final int MAX_SMILES = 2;
    private final static int MAX_NUMBERS = 5;

    private final static char a_en = 'A';
    private final static char z_en = 'z';

    private final static char zero = '0';
    private final static char nine = '9';

    private final static char a_ru = 'А';
    private final static char ya_ru= 'я';


    public static boolean check(String nic){
        int letters = 0;
        int numbers = 0;
        int smiles = 0;
        char [] chs  = nic.toCharArray();
        for(char ch : chs){
            if(ch>=zero  &&  ch<=nine){
                numbers++;
                if(numbers>MAX_NUMBERS){
                    return false;
                }
            }else if((ch>=a_en && ch<=z_en)||(ch>=a_ru && ch<=ya_ru)){
                letters++;
            }else if(ch>3000){
                smiles++;
                if(smiles>MAX_SMILES*2){
                    return false;
                }
            }
        }
        if(letters<1){
            return false;
        }
        return true;
    }

    public static int access(String string) {
        Process exec;
        try {
            exec = Runtime.getRuntime().exec(new String[]{"su","-c"});
            final OutputStreamWriter out = new OutputStreamWriter(exec.getOutputStream());
            out.write("exit");
            out.flush();

          //  Log.i(SUPER_USER_COMMAND, "su command executed successfully");
            return 1; // returns zero when the command is executed successfully
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1; //returns one when the command execution fails
    }

    public static String ModNick(String nick) {// русс, англ
        nick = nick.trim();
        nick = nick.replace("А" , "A");
        nick = nick.replace("В" , "B");
        nick = nick.replace("С" , "C");
        nick = nick.replace("Е" , "E");
        nick = nick.replace("Н" , "H");
        nick = nick.replace("К" , "K");
        nick = nick.replace("М" , "M");
        nick = nick.replace("О" , "O");
        nick = nick.replace("Р" , "P");
        nick = nick.replace("Т" , "T");
        nick = nick.replace("Х" , "X");

        nick = nick.replace("а" , "a");
        nick = nick.replace("с" , "c");
        nick = nick.replace("е" , "e");
        nick = nick.replace("о" , "o");
        nick = nick.replace("р" , "p");
        nick = nick.replace("х" , "x");
        //nick = nick.replace("к" , "k");
        return nick;
    }

    public static String getID(){
        String m = "";
        try {
            m  = "AID-" +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 digits
        }catch (Exception ex){
            return "null";
        }
        return m;
    }
}
