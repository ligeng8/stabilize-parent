package com.stabilize.client;

import java.net.InetSocketAddress;

import com.stabilize.projo.RpcRequest;
import com.stabilize.projo.RpcResponse;
import com.stabilize.serial.MarShallingCodecFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class StablizeClient extends SimpleChannelInboundHandler<RpcResponse>{

	private RpcResponse response;
	
	private String host;
	private int port;
	
	
	
	public StablizeClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	private Object obj = new Object();
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
		// TODO Auto-generated method stub
		this.response = msg;
		ctx.close();
		obj.notifyAll();
	}

	public RpcResponse send(RpcRequest rpcRequest) {
		NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(nioEventLoopGroup)
			         .channel(NioSocketChannel.class)
			         .handler(new LoggingHandler(LogLevel.INFO))
			         .option(ChannelOption.SO_BACKLOG, 1024)
			         .option(ChannelOption.SO_KEEPALIVE, true)
			         .handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// TODO Auto-generated method stub
							ch.pipeline().addLast(MarShallingCodecFactory.buildMarshallingDecoder())
							.addLast(MarShallingCodecFactory.buildMarshallingEncoder())
							.addLast(StablizeClient.this);
						}
					});
			ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(host, port)).sync();
			channelFuture.channel().writeAndFlush(rpcRequest);
			synchronized (obj) {
				obj.wait();
			}
			if(this.response != null)
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}finally {
			nioEventLoopGroup.shutdownGracefully();
		}
		return response;
	}
}
