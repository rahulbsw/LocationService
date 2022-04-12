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
package io.github.pantomath.location;

import io.github.pantomath.location.common.IP2LocationServer;
import io.github.pantomath.location.config.DBConfig;
import io.github.pantomath.location.services.FinderService;
import lombok.extern.log4j.Log4j2;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.File;
/**
 * <p>Application class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
@Log4j2
public class Application {
    static final String MAXMIND_CITY_DB_PATH="MAXMIND_CITY_DB_PATH";
    static final String MAXMIND_ISP_DB_PATH="MAXMIND_ISP_DB_PATH";
    static final String MAXMIND_ASN_DB_PATH="MAXMIND_ASN_DB_PATH";
    static final String IP2LOCATION_CITY_DB_PATH="IP2LOCATION_CITY_DB_PATH";
    static final String IP2LOCATION_ISP_DB_PATH="IP2LOCATION_ISP_DB_PATH";
    static final String IP2LOCATION_DB_PATH="IP2LOCATION_ASN_DB_PATH";

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
        // Create a new server to listen on port 8080
        DBConfig[] configs=new DBConfig[2];
        Integer port=Integer.parseInt(System.getProperty("PORT","8080"));
        String maxmind_city_db=System.getProperty(MAXMIND_CITY_DB_PATH);
        String maxmind_isp_db=System.getProperty(MAXMIND_ISP_DB_PATH);
        String maxmind_asn_db=System.getProperty(MAXMIND_ASN_DB_PATH);
        String ip2location_city_db=System.getProperty(IP2LOCATION_CITY_DB_PATH);
        String ip2location_isp_db=System.getProperty(IP2LOCATION_ISP_DB_PATH);
        String ip2location_asn_db=System.getProperty(IP2LOCATION_DB_PATH);
        boolean isMaxmind=getURI(maxmind_city_db)!=null;
        boolean isIp2location=getURI(ip2location_city_db)!=null;
        if(isMaxmind && isIp2location)
            configs=new DBConfig[2];
        else if (isMaxmind ||isIp2location)
            configs=new DBConfig[1];
        else
            configs=new DBConfig[0];
        int i=0;
        if(isMaxmind) {
            configs[0] = new DBConfig(DBConfig.TYPE.MAXMIND,
                    getURI(maxmind_city_db),
                    getURI(maxmind_isp_db),
                    getURI(maxmind_asn_db)
            );
            i++;
        }
        if(isIp2location) {
            configs[i] = new DBConfig(DBConfig.TYPE.IP2LOCATION,
                    getURI(ip2location_city_db),
                    getURI(ip2location_isp_db),
                    getURI(ip2location_asn_db)
            );
        }

        IP2LocationServer server = new IP2LocationServer(port, new FinderService(configs));
        server.start();
        server.blockUntilShutdown();
    }

    private static URI getURI(String path){
        if(path!=null)
        {
          try {
             return new File(path).toURI();
          }catch (Exception e){
              //ignore
          }
        }
        return null;
    }
}

