package Reflection;

import java.lang.reflect.Method;
import java.util.Comparator;

public class MyComparator implements Comparator<Method> {

    @Override
    public int compare(Method o1, Method o2) {
        return o1.getAnnotation(Test.class).value() - o2.getAnnotation(Test.class).value();
    }
}
