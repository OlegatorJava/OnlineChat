package Race;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Road extends Stage {
    private boolean finishStage = false;
    private boolean win = false;
    private CyclicBarrier barrier;

    public Road(int length) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
    }

    public Road(int length, boolean finishStage, CyclicBarrier barrier) {
        this.barrier = barrier;
        this.length = length;
        this.description = "Дорога " + length + " метров";
        this.finishStage = finishStage;
    }

    @Override
    public void go(Car c) {
        try {
            System.out.println(c.getName() + " начал этап: " + description);
            Thread.sleep(length / c.getSpeed() * 1000);
            System.out.println(c.getName() + " закончил этап: " + description);

            if(finishStage){
                win(c);
                try {
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void win(Car c) {
        if(!win){
            win = true;
            System.out.println(c.getName() + " : WIN");
        }
    }
}
