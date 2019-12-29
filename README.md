# Race condition

> Race Condition is a behavior inside of a code block which is executed by multiple threads where an order of execution 
> makes a difference in an outcome of the code block. The code block with such behavior is named as Critical Section.

Definition of the race condition by [Wikipedia](https://en.wikipedia.org/wiki/Race_condition) with multiple examples.

Example of the Race condition

| Thread 1 | Value in Thread 1 | Thread 2 | Value in Thread 2 | Value in `AmountHolder` |
|----------|-------------------|----------|-------------------|-------|
|          |                   |          |                   | 1     |
|read a value| 1                |          |                   | 1     |
|increment the value| 2                |          |                   | 1     |
|assign the new value| 2                |          |                   | 2     |

Looks good so far. One thread reads a value, increments and writes back the update value. 
The result is as expected. *And now two threads work simultaneously.*
 
| Thread 1 | Value in Thread 1 | Thread 2 | Value in Thread 2 | Value in `AmountHolder` |
|----------|-------------------|----------|-------------------|-------|
|read a value| 2                |          |                   | 2     |
| |                 |  read a value |    2              | 2     |
|increment the value| 3                |          |                   | 2     |
|assign the new value| 3                |          |                   | 3     |
|                   | |increment the value| 3                | 3     |
|                   | |assign the new value| 3                | 3     |

As you see, finally there is wrong value 3, but should be 4.



### Example of unsafe value modification in a multithread environment
##### Amount holder
```java
public class AmountHolder {

    private double amount;
    
    public AmountHolder(double amount) {
    this.amount = amount;
    }
    
    public double getAmount() {
    return amount;
    }
    
    void setAmount(double amount) {
    this.amount = amount;
    }

}
```

##### Unsafe modifier
```java
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
```

Tricky place above. If you run a test when the code above is called by many thread it will bring you wrong result, 
but sometimes the same code will bring expected result. In other words it's the Race condition. The result depends on how 
the threads execute code. The content of the method add is a `critical section`. Read below how to avoid the race condition.

```text
DEBUG AmountModifier: add operation: amount: 100.000000, add: 1.0, thread Thread-3
DEBUG AmountModifier: add operation: amount: 100.000000, add: 1.0, thread Thread-1
DEBUG AmountModifier: add operation: amount: 100.000000, add: 1.0, thread Thread-2
DEBUG AmountModifier: add operation: amount: 101.000000, add: 1.0, thread Thread-7
DEBUG AmountModifier: add operation: amount: 102.000000, add: 1.0, thread Thread-5
DEBUG AmountModifier: add operation: amount: 103.000000, add: 1.0, thread Thread-9
DEBUG AmountModifier: add operation: amount: 104.000000, add: 1.0, thread Thread-6
DEBUG AmountModifier: add operation: amount: 104.000000, add: 1.0, thread Thread-0
DEBUG AmountModifier: add operation: amount: 105.000000, add: 1.0, thread Thread-4
DEBUG AmountModifier: add operation: amount: 105.000000, add: 1.0, thread Thread-8

junit.framework.AssertionFailedError:
Expected :110.0
Actual   :106.0
```

### How to avoid Race condition
The avoidance sound very simple - you need to be sure the critical section is executed as an atomic operation. 
The atomic operation stars right before the value is read and ends when the value is written back. Synchronization of 
the critical section is one of the ways.

### Change implementation to synchronous
The example below wraps the critical section with synchronized block and now the critical section is executed as an atomic operation.

##### Synchronized version of the amount modifier
```java
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
```

After the test execution we can see that the result is good now and we met expected value.

##### Test output
```text
DEBUG AmountSynchronizedModifier: add operation: amount: 100.000000, add: 1.0, thread Thread-1
DEBUG AmountSynchronizedModifier: add operation: amount: 101.000000, add: 1.0, thread Thread-9
DEBUG AmountSynchronizedModifier: add operation: amount: 102.000000, add: 1.0, thread Thread-8
DEBUG AmountSynchronizedModifier: add operation: amount: 103.000000, add: 1.0, thread Thread-7
DEBUG AmountSynchronizedModifier: add operation: amount: 104.000000, add: 1.0, thread Thread-3
DEBUG AmountSynchronizedModifier: add operation: amount: 105.000000, add: 1.0, thread Thread-5
DEBUG AmountSynchronizedModifier: add operation: amount: 106.000000, add: 1.0, thread Thread-4
DEBUG AmountSynchronizedModifier: add operation: amount: 107.000000, add: 1.0, thread Thread-0
DEBUG AmountSynchronizedModifier: add operation: amount: 108.000000, add: 1.0, thread Thread-6
DEBUG AmountSynchronizedModifier: add operation: amount: 109.000000, add: 1.0, thread Thread-2
```

I believe I need to write here a small remark about the synchronization. When we synchronize some code it becomes 
a bottle-neck, because all threads need to execute it, but the threads will be queued and accessed the code one by one. 
The synchronized block in this case should be as short as it possible to avoid huge performance issues. Another good approach 
to separate one synchronized block onto smaller if possible.

### Another option - atomic
In the package `java.util.concurrent.atomic` you can find classes which allows to make atomic changes in `boolean`, `int`, 
`long` variables/fields and general operations `get`, `set`, `increment`, `decrement` are thread-safe.
