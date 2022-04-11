/**
 * The MIT License
 * Copyright © 2022 Project Location Service using GRPC and IP lookup
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.pantomath.location.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * <p>IPAddress class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class IPAddress {
    final InetAddress inetAddress;
    final boolean isPrivate;
    final boolean isIPv4;
    final boolean isIPv6;

    /**
     * <p>Constructor for IPAddress.</p>
     *
     * @param ipAddress a {@link java.lang.String} object
     * @throws java.net.UnknownHostException if any.
     */
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

    /**
     * <p>create.</p>
     *
     * @param ipAddress a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    public static Optional<IPAddress> create(String ipAddress) {
        try {
            return Optional.of(new IPAddress(ipAddress));
        } catch (UnknownHostException e) {
            return Optional.empty();
        }
    }

    /**
     * <p>isIPv4.</p>
     *
     * @param ipAddress a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean isIPv4(String ipAddress) {
        return this.isIPv4;
    }

    /**
     * <p>isIPv6.</p>
     *
     * @param ipAddress a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean isIPv6(String ipAddress) {
        return this.isIPv6;
    }

    /**
     * <p>Getter for the field <code>inetAddress</code>.</p>
     *
     * @return a {@link java.net.InetAddress} object
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * <p>getIPAddress.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getIPAddress() {
        return inetAddress.getHostAddress();
    }

    /**
     * <p>isPrivate.</p>
     *
     * @return a boolean
     */
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
