package equipments.HEM.registration;

public interface RegistrationI {
	public boolean registered(String uid) throws Exception;
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception;
	public void unregister(String uid) throws Exception;
}
