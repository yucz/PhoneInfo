package com.example.xiake.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;


public class ExampleUnitTest {
    @Test
    public void stringSplit() throws Exception {
       String text="12.5434,232.45553454";
       String arr[]=text.split(",");
       System.out.println(arr[0]+","+arr[1]);
    }


}