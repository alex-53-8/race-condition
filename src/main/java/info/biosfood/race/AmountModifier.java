package info.biosfood.race;

import org.apache.log4j.Logger;

public class AmountModifier {

    public static final Logger LOG = Logger.getLogger(AmountModifier.class);

    private AmountHolder amountHolder;

    public AmountModifier(AmountHolder amountHolder) {
        this.amountHolder = amountHolder;
    }

    public void add(double value) {
        double amountBefore = amountHolder.getAmount();

        LOG.debug(String.format("add operation: amount: %f, add: %s, thread %s", amountBefore, value, Thread.currentThread().getName()));

        double amount = amountBefore + value;
        amountHolder.setAmount(amount);
    }

}
