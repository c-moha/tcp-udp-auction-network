package Common;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rq;
    private String name;
    private String role;
    private String ipAddress;
    private String udpPort;
    private String tcpPort;
    private String Password;

    public UserInfo() {
    };

    public UserInfo(String rq, String name, String role, String ipAddress, String udpPort, String tcpPort,
            String password) {
        this.rq = rq;
        this.name = name;
        this.role = role;
        this.ipAddress = ipAddress;
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
        this.Password = password;
    }

    // Setters
    public void setRQ(String rq) {
        this.rq = rq;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUDP(String udpPort) {
        this.udpPort = udpPort;
    }

    public void setTCP(String tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void setPassword(String pw) {
        this.Password = pw;
    }

    // Gets
    public String getRq() {
        return rq;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUdpPort() {
        return udpPort;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public String getPassword() {
        return Password;
    }

    @Override
    public String toString() {
        return "UserInfo{" + "rq='" + rq + '\'' + ", name='" + name + '\'' + ", role='" + role + '\'' + ", ipAddress='"
                + ipAddress + '\'' + ", udpPort='" + udpPort + '\'' + ", tcpPort='" + tcpPort + '\'' + '}';
    }
}
