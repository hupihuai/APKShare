package com.weihu.apkshare.http

import com.weihu.apkshare.APKShareApp
import com.weihu.apkshare.local.LocalService
import com.weihu.apkshare.util.AppUtils
import com.weihu.apkshare.util.IOStreamUtils
import com.weihu.apkshare.vo.FileInfo
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.handler.codec.http.*
import io.netty.handler.ssl.SslHandler
import io.netty.handler.stream.ChunkedFile
import io.netty.util.CharsetUtil
import java.io.File
import java.io.RandomAccessFile
import java.net.InetAddress


/**
 * Created by hupihuai on 2017/12/15.
 */
@ChannelHandler.Sharable
class NettyRequestDispatcher : ChannelInboundHandlerAdapter() {
    private var result = ""

    /*
     * 收到消息时，返回信息
     */
    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("收到请求。。。。。")
        if (msg !is FullHttpRequest) {
            result = "未知请求!"
            send(ctx, result, HttpResponseStatus.BAD_REQUEST)
            return
        }
        try {
            val path = msg.uri() //获取路径
            println("request url = $path")
            val body = getBody(msg)     //获取参数
            val method = msg.method()//获取请求方法
            //首页
            if ("/index".equals(path, ignoreCase = true)
                    || "/index.html".equals(path, ignoreCase = true)
                    || "/".equals(path, ignoreCase = true)) {
                sendIndex(ctx, msg)
                return
            }

            //如果不是这个路径，就直接返回错误
            if (!"/test".equals(path, ignoreCase = true)) {
                result = "非法请求!"
//                send(ctx, result, HttpResponseStatus.BAD_REQUEST)
                var name = AppUtils.getFilePathByName("edu.wuwang.opengl")
                download(ctx, msg, name.absolutePath)
                return
            }
            println("接收到:$method 请求")
            //如果是GET请求
            if (HttpMethod.GET == method) {
                //接受到的消息，做业务逻辑处理...
                println("body:" + body)
                result = "GET请求"
                send(ctx, result, HttpResponseStatus.OK)
                return
            }
            //如果是POST请求
            if (HttpMethod.POST == method) {
                //接受到的消息，做业务逻辑处理...
                println("body:" + body)
                result = "POST请求"
                send(ctx, result, HttpResponseStatus.OK)
                return
            }

            //如果是PUT请求
            if (HttpMethod.PUT == method) {
                //接受到的消息，做业务逻辑处理...
                println("body:" + body)
                result = "PUT请求"
                send(ctx, result, HttpResponseStatus.OK)
                return
            }
            //如果是DELETE请求
            if (HttpMethod.DELETE == method) {
                //接受到的消息，做业务逻辑处理...
                println("body:" + body)
                result = "DELETE请求"
                send(ctx, result, HttpResponseStatus.OK)
                return
            }
        } catch (e: Exception) {
            println("处理请求失败!")
            e.printStackTrace()
        } finally {
            //释放请求
            msg.release()
        }
    }

    private fun sendIndex(ctx: ChannelHandlerContext, request: FullHttpRequest) {
        var indexStream = APKShareApp.instance.assets.open("index.html")
        var content = IOStreamUtils.inputStreamToString(indexStream)

        var fileTemplateStream = APKShareApp.instance.assets.open("file.template")
        var fileTemplateStr = IOStreamUtils.inputStreamToString(fileTemplateStream)
        fileTemplateStr?.replace("{file_size}", "5M")
        fileTemplateStr?.replace("{file_path}", AppUtils.getFilePathByName("edu.wuwang.opengl").absolutePath)

        content = content?.replace("{file_list_template}", fileTemplateStr!!)
        val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8))
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    }

    private fun getShareFileList(): List<FileInfo> {
        var fileList = LocalService.getAPKFromAPKDirectory()
        return fileList
    }

    /**
     * 获取body参数
     * @param request
     * @return
     */
    private fun getBody(request: FullHttpRequest): String {
        val buf = request.content()
        return buf.toString(CharsetUtil.UTF_8)
    }

    /**
     * 发送的返回值
     * @param ctx     返回
     * @param context 消息
     * @param status 状态
     */
    private fun send(ctx: ChannelHandlerContext, context: String, status: HttpResponseStatus) {
        val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8))
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    }

    private fun download(ctx: ChannelHandlerContext, request: FullHttpRequest, path: String) {
        val file = File(path)
        val raf = RandomAccessFile(file, "r")
        val fileLength = raf.length()
        val response = DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        HttpHeaders.setContentLength(response, fileLength)
        setContentTypeHeader(response, file)
        if (HttpHeaders.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
        }
        ctx.write(response)

        // Write the content.
        val sendFileFuture: ChannelFuture
        if (ctx.pipeline().get(SslHandler::class.java) == null) {
            sendFileFuture = ctx.write(DefaultFileRegion(raf.channel, 0, fileLength), ctx.newProgressivePromise())
        } else {
            sendFileFuture = ctx.write(HttpChunkedInput(ChunkedFile(raf, 0, fileLength, 8192)), ctx.newProgressivePromise())
        }
        sendFileFuture.addListener(object : ChannelProgressiveFutureListener {
            override fun operationProgressed(future: ChannelProgressiveFuture, progress: Long, total: Long) {
                if (total < 0) { // total unknown
                    System.err.println(future.channel().toString() + " Transfer progress: " + progress)
                } else {
                    System.err.println(future.channel().toString() + " Transfer progress: " + progress + " / " + total)
                }
            }

            override fun operationComplete(future: ChannelProgressiveFuture) {
                System.err.println(future.channel().toString() + " Transfer complete.")
            }
        })

        // Write the end marker
        val lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)

        // Decide whether to close the connection or not.
        if (!HttpHeaders.isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE)
        }
    }

    private fun setContentTypeHeader(response: HttpResponse, file: File) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "multipart/form-data")
        response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment;fileName=opengl.apk")
    }

    /*
     * 建立连接时，返回消息
     */
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        println("连接的客户端地址:" + ctx.channel().remoteAddress())
        ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ")
        super.channelActive(ctx)
    }


}