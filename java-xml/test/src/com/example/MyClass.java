package com.example;

public class MyClass {
    
    public final static String A = "asdasd";
    private StringBuffer sb = new StringBuffer();
    private int x = Integer.valueOf(sb.toString());
    
    public static void main() {
        System.out.println(MyClass.A);
    }
    
    private static class MyClass2 {
        
        public void foo() {
            System.out.println(MyClass.A);
        }
        
    }
    

}
