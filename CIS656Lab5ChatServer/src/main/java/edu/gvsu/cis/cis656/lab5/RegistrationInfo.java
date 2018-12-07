package edu.gvsu.cis.cis656.lab5;//----------------------------------------------------------------------
//
//  Filename: edu.gvsu.cis.cis656.lab5.RegistrationInfo.java
//  Description: 
// 
//  $Id:$
//
//----------------------------------------------------------------------

/**
 * @author Jonathan Engelsma
 *
 */


/**
 * This class represents the information that the chat client registers
 * with the presence server.
 */
public class RegistrationInfo 
{
    private String userName;
    private String host;
    private boolean status;
    private int port;

    public RegistrationInfo() 
    {
    	this.userName = null;
    	this.host = null;
    	this.status = false;
    	this.port = -1;
    }
    
    /**
     * edu.gvsu.cis.cis656.lab5.RegistrationInfo  constructor.
     * @param uname Name of the user being registered.
     * @param h Name of the host their client is running on.
     * @param p The port # their client is listening for connections on.
     * @param s The status, true if the client is available to host a game, false otherwise.
     */
    public RegistrationInfo(String uname, String h, int p, boolean s)
    {
        this.userName = uname;
        this.host = h;
        this.port = p;
        this.status = s;
    }

    /**
     * Determine the name of the user.
     * @return The name of the user.
     */
    public String getUserName()
    {
        return this.userName;
    }

    
    /**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
     * Determine the host the user is on.
     * @return The name of the host client resides on.
     */
    public String getHost()
    {
        return this.host;
    }

    
    /**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
     * Get the port the client is listening for connections on.
     * @return port value.
     */
    public int getPort()
    {
        return this.port;
    }
    
    
    
    /**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
     * Get the status of the client - true means availability, false means don't disturb.
     * @return status value.
     */
    public boolean getStatus()
    {
    	return this.status;
    }
    
    /**
     * Modify the user's busy/available status.
     * @param status set to true if user is available, false otherwise.
     */
	public void setStatus(boolean status) {
		this.status = status;
	}
    
	/**
	 * hashCode() and equals() use this.  Don't touch it!  
	 */
	public String toString()
	{
		return this.userName + "@" + this.host + ":" + this.port;
	}


}