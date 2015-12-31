package com.maykot.radiolibrary;

public class MessageFragment {

	private String device64BitAddress;
	private int qtdPackages;
	private int numPackge;
	private byte[] fragment;

	public String getDevice64BitAddress() {
		return device64BitAddress;
	}

	public void setDevice64BitAddress(String device64BitAddress) {
		this.device64BitAddress = device64BitAddress;
	}

	public int getQtdPackages() {
		return qtdPackages;
	}

	public void setQtdPackages(int qtdPackages) {
		this.qtdPackages = qtdPackages;
	}

	public int getNumPackge() {
		return numPackge;
	}

	public void setNumPackge(int numPackge) {
		this.numPackge = numPackge;
	}

	public byte[] getFragment() {
		return fragment;
	}

	public void setFragment(byte[] fragment) {
		this.fragment = fragment;
	}

}
