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
        Map<Integer, Method> testMap = new TreeMap<>();
        List<Method> methodCompare = new ArrayList<>();
        MyComparator myComparator = new MyComparator();
        TestClass test = new TestClass();
        Method[] methods = testClass.getDeclaredMethods();
        boolean before = false;
        boolean after = false;

        //Проверяем на единоличность методов с аннотациями BeforeSuite и AfterSuite
        //Запускаем первый метод
        for (Method method : methods) {
            if (method.getAnnotation(BeforeSuite.class) != null) {
                if (before) {
                    throw new RuntimeException();
                }
                before = true;
                method.invoke(test);
                continue;
            }
            if (method.getAnnotation(AfterSuite.class) != null) {
                if (after) {
                    throw new RuntimeException();
                }
                after = true;
            }
        }

        //Заносим методы в лист
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                methodCompare.add(method);
            }
        }

        //Выстраеваем методы с аннотацией Test по порядку
        methodCompare.sort(myComparator);

        //Запускаем методы с аннотацией Test по порядку
        for (Method method : methodCompare) {
            method.invoke(test);
        }

        //Запускаем последний метод
        for (Method method : methods) {
            if (method.getAnnotation(AfterSuite.class) != null) {
                method.invoke(test);
            }
        }
    }
}
