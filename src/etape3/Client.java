
import java.lang.reflect.InvocationTargetException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Client extends UnicastRemoteObject implements Client_itf {
	
	private static HashMap<Integer,SharedObject> objects;
	private static Server_itf s;
	private static Client c;

	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		objects = new HashMap<Integer,SharedObject>();
		try {
			c = new Client();
			s = (Server_itf) Naming.lookup("//localhost:1099/server");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		SharedObject so = null;
		try {
			Integer id = s.lookup(name);
			if (id != 0) {
				so = objects.get(id);
				if (so == null) {;
					try {
					    Class[] args = new Class[1];
					    args[0] = int.class;
						so = (SharedObject) StubGenerator.getStub(s.getClass(id)).getDeclaredConstructor(args).newInstance(id);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | CompilationFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					objects.put(id,so);
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return so;
	}	
	
	// lookup the sharedobject to rebind the reference after deserialization
	public static SharedObject lookup(int id) {
		SharedObject so = null;
		so = objects.get(id);
		if (so == null) {
			try {
			    Class[] args = new Class[1];
			    args[0] = int.class;
				so = (SharedObject) StubGenerator.getStub(s.getClass(id)).getDeclaredConstructor(args).newInstance(id);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | CompilationFailedException | RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			return so;
	}	
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			SharedObject o = (SharedObject) so;
			int id = o.id;
			s.register(name,id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		int id = 0;
		try {
			id = s.create(o);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SharedObject so = null;
		try {
			StubGenerator.getStub(o.getClass());
		    Class[] args = new Class[1];
		    args[0] = int.class;
			so = (SharedObject) StubGenerator.getStub(o.getClass()).getDeclaredConstructor(args).newInstance(id);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | CompilationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		objects.put(id, so);
		return so;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		Object o = null;
		try {
			o = s.lock_read(id, c);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		Object o = null;
		try {
			o = s.lock_write(id, c);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
	  	System.out.println("reduce_lock");
		return objects.get(id).reduce_lock();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
	  	System.out.println("invalidate_reader");
		objects.get(id).invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
	  	System.out.println("invalidate_writer");
		return objects.get(id).invalidate_writer();
	}
	
}
