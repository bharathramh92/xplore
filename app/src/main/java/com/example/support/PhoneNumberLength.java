package com.example.support;

import java.util.HashMap;

/**
 * Created by bharathramh on 4/12/15.
 */
public class PhoneNumberLength {
    public static int check(String countryCode){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("1",10);
        map.put("91",10);
        try{
            return map.get(countryCode);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
