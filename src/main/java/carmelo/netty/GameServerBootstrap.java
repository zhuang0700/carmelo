package carmelo.netty;

import carmelo.servlet.Servlet;
import carmelo.common.Configuration;
import carmelo.netty.http.HttpServerInitializer;
import carmelo.netty.tcp.TcpServerInitializer;
import carmelo.netty.websocket.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * game server bootstrap
 * 
 * @author needmorecode
 *
 */
public class GameServerBootstrap {

	public void run() {

		// init servlet
		final Servlet servlet = new Servlet();
		servlet.init();

		// camelo supports both tcp and http
		
		// http channel
		new Thread() {
			public void run() {
				EventLoopGroup bossGroup2 = new NioEventLoopGroup();
				EventLoopGroup workerGroup2 = new NioEventLoopGroup();
				try {
					ServerBootstrap b2 = new ServerBootstrap();
					b2.group(bossGroup2, workerGroup2)
							.channel(NioServerSocketChannel.class)
							.childHandler(new HttpServerInitializer(servlet));
					int port = Integer.parseInt(Configuration.getProperty(Configuration.HTTP_PORT));
					b2.bind(port).sync().channel().closeFuture().sync();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					bossGroup2.shutdownGracefully();
					workerGroup2.shutdownGracefully();
				}
			}
		}.start();
		
/*		// http push channel
		new Thread() {
			public void run() {
				EventLoopGroup bossGroup2 = new NioEventLoopGroup(1);
				EventLoopGroup workerGroup2 = new NioEventLoopGroup();
				try {
					ServerBootstrap b2 = new ServerBootstrap();
					b2.group(bossGroup2, workerGroup2)
							.channel(NioServerSocketChannel.class)
							.childHandler(new HttpServerInitializer(servlet));
					int port = Integer.parseInt(Configuration.getProperty(Configuration.HTTP_PUSH_PORT));
					b2.bind(port).sync().channel().closeFuture().sync();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					bossGroup2.shutdownGracefully();
					workerGroup2.shutdownGracefully();
				}
			}
		}.start();*/

		// tcp channel
		new Thread() {
			public void run() {
				final EventLoopGroup bossGroup = new NioEventLoopGroup();
				final EventLoopGroup workerGroup = new NioEventLoopGroup();
				//final EventExecutorGroup businessGroup = new DefaultEventExecutorGroup(4);
				try {
					ServerBootstrap b1 = new ServerBootstrap();
					b1.group(bossGroup, workerGroup)
							.channel(NioServerSocketChannel.class)
							.childHandler(new TcpServerInitializer(servlet));
					int port = Integer.parseInt(Configuration.getProperty(Configuration.TCP_PORT));
					b1.bind(port).sync().channel().closeFuture().sync();
		
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
				}
			}
		}.start();
		
		// websocket channel
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                           .childHandler(new WebSocketServerInitializer(servlet));
            int port = Integer.parseInt(Configuration.getProperty(Configuration.WEBSOCKET_PORT));
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

	}

}
