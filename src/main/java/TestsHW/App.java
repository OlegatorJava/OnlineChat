package TestsHW;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class App {
private static Random random = new Random();
private static ArrayList<Integer> list = new ArrayList<>();
    public static void main(String[] args) {

    }

    public static ArrayList<Integer> listInteger(ArrayList<Integer> list){
        ListIterator<Integer> listIterator = list.listIterator(list.size());
        ArrayList<Integer> result = new ArrayList<>();
        boolean four = false;
        while(listIterator.hasPrevious()){
            Integer previous = listIterator.previous();
            if(previous == 4){
                four = true;
                break;
            }
            result.add(0, previous);

        }
        if(!four){
            throw new RuntimeException("В переданном массиве не найдено 4");
        }
        return result;
    }

    public boolean checkingArray(ArrayList<Integer> list){
        boolean four = false;
        boolean one = false;
        boolean n = false;
        for (Integer integer : list) {
            if(integer == 1){
                one = true;
            }else if(integer == 4){
                four = true;
            }else{
                n = true;
            }
        }
        if(!four || !one || n){
            return false;
        }
        return true;
    }
}
