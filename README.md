# DNSHole - Base
This is a pretty old project of mine where I implemented the DNS protocol in Java. This is the foundation for a pihole-like application, featuring a web-frontend & JavaFX desktop application.

> This code is relatively old so I verifying everything before using it. I was also pretty unexperienced at the time of writing.
The project provides data structures for DNS messages & a small tcp/udp server implementation.

# Example
```java
try(NettyDNSServerTCP tcp = new NettyDNSServerTCP()) {
	tcp.setRequestProcessor(new UDPForward(new InetSocketAddress("8.8.8.8", 53)) {
				
		@Override
		public void processRequest(ServerPeer peer, DNSMessage message) throws Exception {
			final DNSMessage response = new DNSMessage(this.forward(message));
					
			peer.send(response.toMessage());
		};
				
	});
			
	tcp.bind(new InetSocketAddress("0.0.0.0", 53)).sync();
			
	try(NettyDNSServer udp = new NettyDNSServer()) {
		udp.setRequestProcessor(new UDPForward(new InetSocketAddress("8.8.8.8", 53)) {
					
			@Override
			public void processRequest(ServerPeer peer, DNSMessage message) throws Exception {
				final DNSMessage response = new DNSMessage(this.forward(message));
						
				peer.send(response.toMessage());
			};
					
		});
				
		udp.bind(new InetSocketAddress("0.0.0.0", 53)).channel().closeFuture().sync();
	}
}
```
