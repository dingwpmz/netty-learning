/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.prestigeding.remoting.netty;

import com.alibaba.fastjson.JSON;
import com.prestigeding.remoting.Constant;
import com.prestigeding.remoting.common.RemotingUtil;
import com.prestigeding.remoting.protocol.CustomBody;
import com.prestigeding.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    private static final Logger log = LoggerFactory.getLogger(Constant.ROCKETMQ_REMOTING);

    @Override
    public void encode(ChannelHandlerContext ctx, RemotingCommand cmd, ByteBuf out)
        throws Exception {

        ByteBuf body = null;
        try {
            //  4字节长度 + 2字节扩展头部（2字节flag[最低位：请求类型，请求或响应] ） + 2字节扩展属性长度 + N字节扩展属性 + custom body
            CustomBody customBody = cmd.getCustomBody();
            if(customBody != null ) {
                body = customBody.encode();
            }
            byte[] extData = JSON.toJSONBytes(cmd);
            int bodyLen = (body == null ? 0 : body.readableBytes() ) + extData.length + 2; // +2 表示2字节扩展属性长度
            out.writeInt( bodyLen );
            out.writeShort( cmd.isResponseType() ? 1 : 0 );
            out.writeShort(extData.length);
            out.writeBytes(extData);
            if(body != null ) {
                out.writeBytes(body);
            }
        } catch (Exception e) {
            log.error("encode exception, ", e);
            RemotingUtil.closeChannel(ctx.channel());
        } finally {
            if(body != null) {
                body.release();
            }
        }
    }
}
