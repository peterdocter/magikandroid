/**
 * 
 */
package com.magik.activities;

/**
 * @author User
 *
 */
public class InterfaceManager {
	
	private static InterfaceManager instance;
	
	private boolean mWeb;
	
	private boolean mFiles;
	
	private boolean mAct;
	
	public InterfaceManager()
	{
		mWeb = false;
		
		mFiles = false;
		
		mAct = false;
	}
	
	public static InterfaceManager getInstance()
	{
		if(instance == null)
		{
			instance = new InterfaceManager();
		}
		return instance;
	}

	/**
	 * @return the mWeb
	 */
	public boolean ismWeb() {
		return mWeb;
	}

	/**
	 * @param mWeb the mWeb to set
	 */
	public void setmWeb(boolean mWeb) {
		this.mWeb = mWeb;
	}

	/**
	 * @return the mFiles
	 */
	public boolean ismFiles() {
		return mFiles;
	}

	/**
	 * @param mFiles the mFiles to set
	 */
	public void setmFiles(boolean mFiles) {
		this.mFiles = mFiles;
	}

	/**
	 * @return the mAct
	 */
	public boolean ismAct() {
		return mAct;
	}

	/**
	 * @param mAct the mAct to set
	 */
	public void setmAct(boolean mAct) {
		this.mAct = mAct;
	}
	
	
	

}
