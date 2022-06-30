package Reflection;

public class TestClass {
    @BeforeSuite
    public void beforeMethod(){
        System.out.println("beforeSuite");
    }
    @Test(8)
    public void method1(){
        System.out.println("method1");
    }
    @Test(8)
    public void method2(){
        System.out.println("method2");
    }
    @AfterSuite
    public void afterMethod(){
        System.out.println("AfterSuite");
    }
    @Test(8)
    public void method3(){
        System.out.println("method3");
    }


}
