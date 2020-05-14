package com.weihu.apkshare.http

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.stream.ChunkedWriteHandler




/**
 * Created by hupihuai on 2017/12/15.
 */
object DefaultHttpServer {


    @Throws(InterruptedException::class)
    fun start(port: Int) {

        // start the server here.
        val bossGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()

        try {
            val serverBootstrap = ServerBootstrap()
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(channel: SocketChannel) {
                            channel.pipeline()
                                    .addLast("request-decoder", HttpRequestDecoder())
                                    // transfer different http message
                                    .addLast("post", HttpObjectAggregator(1024 * 1024))
                                    .addLast("response-encoder", HttpResponseEncoder())
                                    .addLast("handler", NettyRequestDispatcher())
                                    .addLast(ChunkedWriteHandler())
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)

            val future = serverBootstrap.bind(port).sync()
            println("Server starts at port:" + port)
            future.channel().closeFuture().sync()
            println("Server end at port:" + port)
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

}