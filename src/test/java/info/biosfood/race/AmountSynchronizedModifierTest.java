package info.biosfood.race;

import info.biosfood.race.test.ManyThreadsSimultaneously;
import info.biosfood.race.test.ManyThreadsSimultaneouslyBuilder;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AmountSynchronizedModifierTest {

    AmountHolder amountHolder;
    AmountSynchronizedModifier amountModifier;

    @Before
    public void setup() {
        amountHolder = new AmountHolder(100.0);
        amountModifier = new AmountSynchronizedModifier(amountHolder);
    }

    @Test
    public void testMultiThreads() {
        ManyThreadsSimultaneously threads = ManyThreadsSimultaneouslyBuilder.create()
                .repeat(10, () -> {
                    try {
                        Thread.sleep(50);
                    } catch(Exception e) {}

                    amountModifier.add(1d);
                })
                .build();

        threads.execute();

        try{
            Thread.sleep(1000);
        } catch(Exception e) {

        }

        assertEquals(110.0, amountHolder.getAmount());
    }

}
