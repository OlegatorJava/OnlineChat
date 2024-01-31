package TestsHW;

import com.sun.source.doctree.IndexTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class AppTest {
    private App app;


    @BeforeEach
    void init(){
        app = new App();
    }

@MethodSource("createList")
@ParameterizedTest
    void listIntegerTest(ArrayList<Integer> list, ArrayList<Integer> result){
        Assertions.assertEquals(result, app.listInteger(list));
    }

    public static Stream<Arguments> createList (){
        List<Arguments> arguments = new ArrayList<>();
        arguments.add(Arguments.arguments(
                new ArrayList<>(Arrays.asList(5, 2, 0, 4, 1, 2, 8, 7)),
                new ArrayList<>(Arrays.asList(1, 2, 8, 7))));
        arguments.add(Arguments.arguments(
                new ArrayList<>(Arrays.asList(4,6,7,4,3,2,4,0)),
                new ArrayList<>(Arrays.asList(0))));
        arguments.add(Arguments.arguments(
                new ArrayList<>(Arrays.asList(-2,-200,4000,5,4,769,3)),
                new ArrayList<>(Arrays.asList(769,3))));
        arguments.add(Arguments.arguments(
                new ArrayList<>(Arrays.asList(-55,4,0,0,0,1,0,1,4)),
                new ArrayList<>(Arrays.asList())));

        return arguments.stream();
    }
    @Test
    void testException(){
        Assertions.assertThrows(
                RuntimeException.class,
                () -> app.listInteger(new ArrayList<>(Arrays.asList(-2,-200,4000,5,3,769,3))));
    }

    @MethodSource("createCheckingArray")
    @ParameterizedTest
    void testChecking(boolean result,ArrayList<Integer> list){
        Assertions.assertEquals(result, app.checkingArray(list));
    }

    public static Stream<Arguments> createCheckingArray(){
        List<Arguments> arguments = new ArrayList<>();
        arguments.add(Arguments.arguments(false, new ArrayList<>(Arrays.asList(1,1,1,1,3,1,3))));
        arguments.add(Arguments.arguments(true, new ArrayList<>(Arrays.asList(1,1,1,1,4,1,4))));
        arguments.add(Arguments.arguments(false, new ArrayList<>(Arrays.asList(1,1,1,1,3,1,3,4))));
        arguments.add(Arguments.arguments(false, new ArrayList<>(Arrays.asList(1,1,1,1))));
        arguments.add(Arguments.arguments(false, new ArrayList<>(Arrays.asList(0,0,0,0,0,0))));

        return arguments.stream();
    }
}
