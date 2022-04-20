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
package io.github.pantomath.location.store;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StoreUtils {
    static Map<String,String>  ISO2_CODE_2_CONTINENT=new ConcurrentHashMap<>();
    static {
        Gson gson=new Gson();
        Reader reader = null;
        try{
            reader = Files.newBufferedReader(Paths.get(StoreUtils.class.getResource("continent1.json").toURI()));
        } catch (Exception e) {

            String urlPath =  "http://country.io/continent.json";
            try {
                URL url = new URL(urlPath);
                File file = new File("continent.json");
                FileUtils.copyURLToFile(url, file);
                reader = Files.newBufferedReader(file.toPath());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        ISO2_CODE_2_CONTINENT = gson.fromJson(reader, ISO2_CODE_2_CONTINENT.getClass());

    }
    public static String getContinent(String iso2_code){
        if(Strings.isEmpty(iso2_code))
            return null;
        return  ISO2_CODE_2_CONTINENT.get(iso2_code);
    }
}
