package com.sky.search.transformer.json.transformmethods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pair on 15/04/2016.
 */
public class MyTest {

    public static void main(String[] args) {
        String msDate = "1460642168026";
        try {
            Date convertedDate = new SimpleDateFormat("SSSSSSSSSSSSS").parse(msDate);
            System.out.println(convertedDate);
            System.out.println(new Date(1460642168026L));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
