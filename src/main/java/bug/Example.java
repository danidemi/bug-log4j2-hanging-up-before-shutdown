package bug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Example {

    private static final Logger logger = LogManager.getLogger(Example.class);

    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i<2000; i++){
            logger.info("This is log message #{}.", i);
            Thread.sleep(0);
        }
    }

}
