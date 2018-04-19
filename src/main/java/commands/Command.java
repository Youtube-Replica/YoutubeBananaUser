package commands;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public abstract class Command implements Runnable {

    protected HashMap<String, Object> parameters;

    final public void init(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    protected abstract void execute() throws NoSuchAlgorithmException;

    final public void run() {
        try {
            this.execute();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
