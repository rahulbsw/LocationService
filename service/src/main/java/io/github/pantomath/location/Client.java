package io.github.pantomath.location;

import io.github.pantomath.location.common.CityResponses;
import io.github.pantomath.location.common.FindGrpc;
import io.github.pantomath.location.common.IPAddresses;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;

public class Client {
    public static void main( String[] args ) throws Exception
    {
        // Channel is the abstraction to connect to a service endpoint
        // Let's use plaintext communication because we don't have certs
        final ManagedChannel channel = NettyChannelBuilder.forTarget("localhost:8080").usePlaintext().build();

        // It is up to the client to determine whether to block the call
        // Here we create a blocking stub, but an async stub,
        // or an async stub with Future are always possible.
        FindGrpc.FindBlockingStub stub = FindGrpc.newBlockingStub(channel);
        IPAddresses.Builder request = IPAddresses.newBuilder()
                .addIp("98.143.133.154")
                .addIp("107.6.171.130")
                .addIp("45.33.66.232")
                .addIp("69.175.97.170")
                .addIp("173.255.213.43");



        // Finally, make the call using the stub
        CityResponses response = stub.cities(request.build());

        System.out.println(response.getCityList());

        // A Channel should be shutdown before stopping the process.
        channel.shutdownNow();
    }
}
