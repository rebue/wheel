package com.zboss.wheel.eo;

/**
 * 网络地址(格式为host:port)
 */
public class NetAddr {
	public NetAddr(String sData) {
		String[] strs = sData.split(":");
		this.ip = strs[0];
		this.port = Integer.parseInt(strs[1]);
	}

	public NetAddr(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public NetAddr(String ip, String port) {
		this.ip = ip;
		this.port = Integer.parseInt(port);
	}

	@Override
	public String toString() {
		return ip + ":" + port;
	}

	private String	ip;
	private int		port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NetAddr other = (NetAddr) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

}
