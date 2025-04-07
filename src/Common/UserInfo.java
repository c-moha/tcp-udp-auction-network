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

    public void setName(String rq) {
        this.rq = rq;
    }

    public void setRole(String rq) {
        this.rq = rq;
    }

    public void setipAddress(String rq) {
        this.rq = rq;
    }

    public void setUDP(String rq) {
        this.rq = rq;
    }

    public void setTCP(String rq) {
        this.rq = rq;
    }

    public void setPassword(String rq) {
        this.rq = rq;
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
