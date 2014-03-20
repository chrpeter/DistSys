
import java.util.ArrayList;

/**
 * A resource with an associated lock that can be held by only one transaction at a time.
 */
class Resource
{
	static final int NOT_LOCKED = -1;

	/**
	 * The transaction currently holding the lock to this resource
	 */
	private int lockOwner;

	/**
	 * Creates a new resource.
	 */
	Resource()
	{
		lockOwner = NOT_LOCKED;
	}

	/**
	 * Gives the lock of this resource to the requesting transaction. Blocks
	 * the caller until the lock could be acquired.
	 *
	 * @param transactionId The ID of the transaction that wants the lock.
	 * @return Whether or not the lock could be acquired.
	 */
	synchronized boolean lock(int transactionId, int resourceId, ServerImpl serverimpl)
	{
		if (lockOwner == transactionId) {
			System.err.println("Error: Transaction " + transactionId + " tried to lock a resource it already has locked!");
			return false;
		}
		long timeout_time = Globals.TIMEOUT_INTERVAL + System.currentTimeMillis();
		while (lockOwner != NOT_LOCKED) {
			try {
				if (Globals.PROBING_ENABLED) {
					Server server = serverimpl.getServer(ServerImpl.getTransactionOwner(lockOwner));
					ProbeMessage probeMessage = new ProbeMessage(transactionId, new ArrayList<Integer>(), server, resourceId);
					probeMessage.start();
					try {
						wait();
					} catch (InterruptedException e) {
						//nothing to see here...
					}
					if(lockOwner != NOT_LOCKED){
						System.out.println("false hehehhehehe");
						return false;
					}
				}
				//If timeout
				else {
					System.out.println("jaja");
					wait(timeout_time - System.currentTimeMillis());
				}
				if(System.currentTimeMillis() >= timeout_time){
					return false;
				}
			} catch (InterruptedException ie) {
				System.out.println("HALLA");
			}
		}

		lockOwner = transactionId;
		return true;
	}

	/**
	 * Releases the lock of this resource.
	 *
	 * @param transactionId The ID of the transaction that wants to release lock.
	 *                      If this transaction doesn't currently own the lock an
	 *                      error message is displayed.
	 * @return Whether or not the lock could be released.
	 */
	synchronized boolean unlock(int transactionId)
	{
		if (lockOwner == NOT_LOCKED || lockOwner != transactionId) {
			System.err.println("Error: Transaction " + transactionId + " tried to unlock a resource without owning the lock!");
			return false;
		}

		lockOwner = NOT_LOCKED;
		// Notify a waiting thread that it can acquire the lock
		notifyAll();
		return true;
	}
	public void printProbeList(ArrayList<Integer> pr, int tr){
		pr.add(tr);
		System.out.println(pr.toString());
	}
	/**
	 * Gets the current owner of this resource's lock.
	 *
	 * @return An Integer containing the ID of the transaction currently
	 * holding the lock, or NOT_LOCKED if the resource is unlocked.
	 */
	synchronized int getLockOwner()
	{
		return lockOwner;
	}
	int getLocalLockOwner(){
		return lockOwner;
	}
	/**
	 * Unconditionally releases the lock of this resource.
	 */
	synchronized void forceUnlock()
	{
		lockOwner = NOT_LOCKED;
		// Notify a waiting thread that it can acquire the lock
		notifyAll();
	}

	/**
	 * Checks if this resource's lock is held by a transaction running on the specified server.
	 *
	 * @param serverId The ID of the server.
	 * @return Whether or not the current lock owner is running on that server.
	 */
	synchronized boolean isLockedByServer(int serverId)
	{
		return lockOwner != NOT_LOCKED && ServerImpl.getTransactionOwner(lockOwner) == serverId;
	}
	synchronized void doAbort(Transaction trans){
		notifyAll();
	}
	synchronized void notifyShiet(Transaction t){
		notifyAll();

	}
}
