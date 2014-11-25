package kvstore;

import static kvstore.KVConstants.*;

import java.io.IOException;
import java.net.Socket;

/**
 * This NetworkHandler will asynchronously handle the socket connections.
 * Uses a thread pool to ensure that none of its methods are blocking.
 */
public class TPCRegistrationHandler implements NetworkHandler {

    private ThreadPool threadpool;
    private TPCMaster master;

    /**
     * Constructs a TPCRegistrationHandler with a ThreadPool of a single thread.
     *
     * @param master TPCMaster to register slave with
     */
    public TPCRegistrationHandler(TPCMaster master) {
        this(master, 1);
    }

    /**
     * Constructs a TPCRegistrationHandler with ThreadPool of thread equal to the
     * number given as connections.
     *
     * @param master TPCMaster to carry out requests
     * @param connections number of threads in threadPool to service requests
     */
    public TPCRegistrationHandler(TPCMaster master, int connections) {
        this.threadpool = new ThreadPool(connections);
        this.master = master;
    }

    /**
     * Creates a job to service the request on a socket and enqueues that job
     * in the thread pool. Ignore any InterruptedExceptions.
     *
     * @param slave Socket connected to the slave with the request
     */
    @Override
    public void handle(Socket slave) {
        // implement me
        TPCRegistrationHandlerRunner job = new TPCRegistrationHandlerRunner(client, master);
        try {
            threadPool.addJob(job);
        }
        catch (InterruptedException e) {
            // ignore any InterruptedExceptions like suggested above
        }
    }
    

    public class TPCRegistrationHandlerRunner implements Runnable {
        private Socket client;
        private TPCMaster master;

        public TPCRegistrationHandlerRunner(Socket client, TPCMaster master) {
            this.client = client;
            this.master = master;
        }
        // implement me
        @Override
        public void run() {
            //KVMessage ack_kvm = new KVMessage(ACK);
            try {
                KVMessage kvm = new KVMessage(client);
                if (kvm.getMsgType().equals(REGISTER)) {
                    master.registerSlave(new TPCSlaveInfo(kvm.getMessage));
                    //response_kvm.sendMessage(client); //do we need to send an ack??
                }
            } catch (KVException kve) {
                // Do nothing
                //response_kvm = kve.getKVMessage();
                //try {
                //    response_kvm.sendMessage(client);
                //} catch (KVException e) {}
            }        
        }

    }
}
