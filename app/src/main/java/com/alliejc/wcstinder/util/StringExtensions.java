package com.alliejc.wcstinder.util;

public class StringExtensions {

    public static String EMPTY = "";

    public static boolean isNullOrEmpty(String value){
        if(value == null){
            return true;
        }else if(value.trim().equals("")){
            return true;
        }else {
            return false;
        }
    }

    public static String capitalizeEachWord(String value) {
        StringBuilder sb = new StringBuilder();

        String[] words = value.split(" ");
        for(int i = 0; i < words.length; i++){
            if(words[i] != null && words[i].length() > 0){
                sb.append(Character.toUpperCase(words[i].charAt(0)));
                sb.append(words[i].substring(1));
                sb.append(" ");
            }
        }

        return sb.toString().trim();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }
}

