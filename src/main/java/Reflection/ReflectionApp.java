package Reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionApp {

    public static void main(String[] args) {
        Class<TestClass> testClass = TestClass.class;
        try {
            start(testClass);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void start(Class testClass) throws InvocationTargetException, IllegalAccessException {
        List<Method> methodCompare = new ArrayList<>();
        MyComparator myComparator = new MyComparator();
        TestClass test = new TestClass();
        Method[] methods = testClass.getDeclaredMethods();
        Method before = null;
        Method after = null;

        //Проверяем на единоличность методов с аннотациями BeforeSuite и AfterSuite

        for (Method method : methods) {
            if ((method.isAnnotationPresent(BeforeSuite.class) && before != null) || (method.isAnnotationPresent(AfterSuite.class) && after != null )) {
                    throw new RuntimeException();
                }
            else if(method.isAnnotationPresent(BeforeSuite.class)){
                before = method;
            }

            else if (method.isAnnotationPresent(AfterSuite.class)) {
                after = method;
            }
            else if (method.isAnnotationPresent(Test.class)) {   //Заносим методы в лист
                methodCompare.add(method);
            }
        }

        //Выстраеваем методы с аннотацией Test по порядку
        methodCompare.sort(myComparator);

        //Запускаем первый метод
        assert before != null;
        before.invoke(test);

        //Запускаем методы с аннотацией Test по порядку
        for (Method method : methodCompare) {
            method.invoke(test);
        }

        //Запускаем последний метод
        assert after != null;
        after.invoke(test);

    }
}
