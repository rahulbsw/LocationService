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
package io.github.pantomath.location.config;

import java.net.URI;


/**
 * <p>DBConfig class.</p>
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class DBConfig {
    public TYPE source;
    public URI CITY_DB_URI;
    public URI ISP_DB_URI;
    public URI ASN_DB_URI;
    /**
     * <p>Constructor for DBConfig.</p>
     *
     * @param source a {@link io.github.pantomath.location.config.DBConfig.TYPE} object
     * @param CITY_DB_URI a {@link java.net.URI} object
     * @param ISP_DB_URI a {@link java.net.URI} object
     * @param ASN_DB_URI a {@link java.net.URI} object
     */
    public DBConfig(TYPE source, URI CITY_DB_URI, URI ISP_DB_URI, URI ASN_DB_URI) {
        this.source = source;
        this.CITY_DB_URI = CITY_DB_URI;
        this.ISP_DB_URI = ISP_DB_URI;
        this.ASN_DB_URI = ASN_DB_URI;
    }

    public enum TYPE {MAXMIND, IP2LOCATION}
}

