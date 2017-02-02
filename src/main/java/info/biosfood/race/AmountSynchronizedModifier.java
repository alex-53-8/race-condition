package info.biosfood.race;

import org.apache.log4j.Logger;

public class AmountSynchronizedModifier {

    public static final Logger LOG = Logger.getLogger(AmountSynchronizedModifier.class);

    private final AmountHolder amountHolder;

    public AmountSynchronizedModifier(AmountHolder amountHolder) {
        this.amountHolder = amountHolder;
    }

    public void add(double value) {
        synchronized (amountHolder) {
            double amountBefore = amountHolder.getAmount();

            LOG.debug(String.format("add operation: amount: %f, add: %s, thread %s", amountBefore, value, Thread.currentThread().getName()));

            double amount = amountBefore + value;
            amountHolder.setAmount(amount);
        }
    }

}
