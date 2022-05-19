package ru.gb.onlinechat;

public class ABCapp {

    private final Object mon = new Object();
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {
        ABCapp abc = new ABCapp();
        Thread a = new Thread(() -> abc.print('A'));
        Thread b = new Thread(() -> abc.print('B'));
        Thread c = new Thread(() -> abc.print('C'));
        a.start();
        b.start();
        c.start();

    }

    public void print(char symbol) {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != symbol) {
                        mon.wait();
                    }
                System.out.print(symbol);
                currentLetter++;
                if(currentLetter == 'D'){
                    currentLetter = 'A';
                }
                mon.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
