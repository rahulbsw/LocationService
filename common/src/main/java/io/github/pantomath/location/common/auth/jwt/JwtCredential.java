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
package io.github.pantomath.location.common.auth.jwt;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.concurrent.Executor;

/**
 * CallCredentials implementation, which carries the JWT value that will be propagated to the
 * server in the request metadata with the "Authorization" key and the "Bearer" prefix.
 *
 * @author rajain5
 * @version $Id: $Id
 */
public class JwtCredential extends CallCredentials {

    private final String subject;

    JwtCredential(String subject) {
        this.subject = subject;
    }

    /** {@inheritDoc} */
    @Override
    public void applyRequestMetadata(final RequestInfo requestInfo, final Executor executor,
                                     final MetadataApplier metadataApplier) {
        // Make a JWT compact serialized string.
        // This example omits setting the expiration, but a real application should do it.
        final String jwt =
                Jwts.builder()
                        .setSubject(subject)
                        .signWith(SignatureAlgorithm.HS256, Constant.JWT_SIGNING_KEY)
                        .compact();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Metadata headers = new Metadata();
                    headers.put(Constant.AUTHORIZATION_METADATA_KEY,
                            String.format("%s %s", Constant.BEARER_TYPE, jwt));
                    metadataApplier.apply(headers);
                } catch (Throwable e) {
                    metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void thisUsesUnstableApi() {
        // noop
    }
}
