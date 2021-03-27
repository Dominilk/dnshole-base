# DNSHole - Base
This repository contains the base-work for the actual DNSHole application.
You can find a java client/server implementation of the DNS protocol here :)

# Easy to use
This library is designed to be easy-to-use and to allow you to change nearly every single detail of
the protocol message. Here is an example showing how to create a simple DNS-proxy:
```java
try(NettyDNSServer server = new NettyDNSServer()) {
	server.setRequestProcessor(new UDPForward(new InetSocketAddress("8.8.8.8", 53)));
	
	server.bind(new InetSocketAddress("0.0.0.0", 53)).channel().closeFuture().sync();
}
```