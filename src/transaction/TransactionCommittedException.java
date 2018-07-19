package transaction;

public class TransactionCommittedException extends Exception {
    public TransactionCommittedException(int Xid, String msg) {
        super("The transaction " + Xid + " aborted:" + msg);
    }
}

