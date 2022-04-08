package io.github.pantomath.location.common;

import com.google.common.base.Strings;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class IP2LookupClient {
    final ManagedChannel channel;
    FindGrpc.FindBlockingStub stub;
    public IP2LookupClient(String hostname, int port, CallCredentials callCredentials) {
        this.channel = NettyChannelBuilder.forTarget(hostname+":"+port).usePlaintext().build();
        if(Objects.nonNull(callCredentials))
            this.stub = FindGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);
        else
            this.stub = FindGrpc.newBlockingStub(channel);
    }

    public IP2LookupClient(String hostname, int port) {
        this(hostname,port,null);
    }

    public IP2LookupClient(String hostname) {
        this(hostname,8080);
    }

    static IP2LookupClient INSTANCE;

    public static IP2LookupClient getOrCreate(String hostname, int port){
        if(Objects.isNull(INSTANCE) || INSTANCE.channel.isShutdown() ||INSTANCE.channel.isTerminated()){
            INSTANCE=new IP2LookupClient(hostname,port);
        }
        return INSTANCE;
    }

    public static IP2LookupClient getOrCreate(String hostname){
        return getOrCreate(hostname,8080);
    }

    public LocationResponse location(String ip){
        if(Strings.isNullOrEmpty(ip))
            return LocationResponse.getDefaultInstance();

        return stub.location(IPAddressExt.newBuilder().setIp(ip).build());
    }

    public CityResponse city(String ip){
        if(Strings.isNullOrEmpty(ip))
            return CityResponse.getDefaultInstance();
        return stub.city(IPAddress.newBuilder().setIp(ip).build());
    }

    public CountryResponse country(String ip){
        if(Strings.isNullOrEmpty(ip))
            return CountryResponse.getDefaultInstance();
        return stub.country(IPAddress.newBuilder().setIp(ip).build());
    }

    public LocationResponses locations(String... ips){
        if(Objects.isNull(ips) && ips.length<0)
            return LocationResponses.getDefaultInstance();
        return stub.locations(IPAddressesExt.newBuilder().addAllIp(Arrays.asList(ips)).build());
    }

    public CityResponses cities(String... ips){
        if(Objects.isNull(ips) && ips.length<0)
            return CityResponses.getDefaultInstance();
        return stub.cities(IPAddresses.newBuilder().addAllIp(Arrays.asList(ips)).build());
    }

    public CountryResponses countries(String... ips){
        if(Objects.isNull(ips) && ips.length<0)
            return CountryResponses.getDefaultInstance();
        return stub.countries(IPAddresses.newBuilder().addAllIp(Arrays.asList(ips)).build());
    }

    public void shutdown() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}
