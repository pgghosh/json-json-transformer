package com.sky.search.transformer.json.transformmethods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pair on 07/04/2016.
 */
public class TestRegex {

    public static void main(String[] args) {
        regexTest2();
    }

    private static void regexTest1() {
        String inputJsonPath = "asset:programme.uuid";
        Pattern pattern = Pattern.compile("^([^:\\s]*):([^:\\s]*)$");
        Matcher matcher = pattern.matcher(inputJsonPath);
        if(!matcher.find()){
            // throw exception
        }
        String jsonRef = matcher.group(1);
        String jsonPath = matcher.group(2);

        System.out.println("jsonRef:"+jsonRef+" , jsonPath:"+jsonPath);
    }

    private static void regexTest2() {
        String inputJsonPath = "programme[2]";
        Pattern pattern = Pattern.compile("^(\\w+)(?:\\[\\d+\\])+$");
        Matcher matcher = pattern.matcher(inputJsonPath);
        if(!matcher.find()){
            // throw exception
        }

        for(int i=0;i<=matcher.groupCount();i++){
            System.out.println("Group ["+i+"] : "+matcher.group(i));
        }

//        System.out.println(matcher.toMatchResult().toString());

        // Analyse indexes
        Matcher mtchr = Pattern.compile("\\[(\\d+)\\]").matcher(inputJsonPath);
        while(mtchr.find())  {
            System.out.print(mtchr.group(1) + " ");
        }


    }
}
