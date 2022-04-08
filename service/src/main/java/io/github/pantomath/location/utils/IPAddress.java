package io.github.pantomath.location.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class IPAddress {
    final InetAddress inetAddress;
    final boolean isPrivate;
    final boolean isIPv4;
    final boolean isIPv6;

    public IPAddress(String ipAddress) throws UnknownHostException {
        this.inetAddress = InetAddress.getByName(ipAddress);
        isIPv4 = (inetAddress instanceof Inet4Address) && inetAddress.getHostAddress().equals(ipAddress);
        isIPv6 = (inetAddress instanceof Inet6Address);
        if (isIPv4)
            this.isPrivate = isPrivateIPv4(ipAddress);
        else if (isIPv6)
            this.isPrivate = isPrivateIPv6(ipAddress);
        else
            this.isPrivate = false;
    }

    public static Optional<IPAddress> create(String ipAddress) {
        try {
            return Optional.of(new IPAddress(ipAddress));
        } catch (UnknownHostException e) {
            return Optional.empty();
        }
    }

    public boolean isIPv4(String ipAddress) {
        return this.isIPv4;
    }

    public boolean isIPv6(String ipAddress) {
        return this.isIPv6;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public String getIPAddress() {
        return inetAddress.getHostAddress();
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    private boolean isPrivateIPv4(String ipAddress) {
        try {
            String[] ipAddressArray = ipAddress.split("\\.");
            int[] ipParts = new int[ipAddressArray.length];
            for (int i = 0; i < ipAddressArray.length; i++) {
                ipParts[i] = Integer.parseInt(ipAddressArray[i].trim());
            }

            switch (ipParts[0]) {
                case 10:
                case 127:
                    return true;
                case 172:
                    return (ipParts[1] >= 16) && (ipParts[1] < 32);
                case 192:
                    return (ipParts[1] == 168);
                case 169:
                    return (ipParts[1] == 254);
            }
        } catch (Exception ex) {
        }

        return false;
    }

    private boolean isPrivateIPv6(String ipAddress) {
        boolean isPrivateIPv6 = false;
        String[] ipParts = ipAddress.trim().split(":");
        if (ipParts.length > 0) {
            String firstBlock = ipParts[0];
            String prefix = firstBlock.substring(0, 2);

            if (firstBlock.equalsIgnoreCase("fe80")
                    || firstBlock.equalsIgnoreCase("100")
                    || ((prefix.equalsIgnoreCase("fc") && firstBlock.length() >= 4))
                    || ((prefix.equalsIgnoreCase("fd") && firstBlock.length() >= 4))) {
                isPrivateIPv6 = true;
            }
        }
        return isPrivateIPv6;
    }
}
