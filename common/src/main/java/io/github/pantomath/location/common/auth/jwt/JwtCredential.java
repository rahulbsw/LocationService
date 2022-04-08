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
 */
public class JwtCredential extends CallCredentials {

    private final String subject;

    JwtCredential(String subject) {
        this.subject = subject;
    }

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

    @Override
    public void thisUsesUnstableApi() {
        // noop
    }
}
