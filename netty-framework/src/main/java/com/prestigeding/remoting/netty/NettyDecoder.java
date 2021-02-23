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
import com.prestigeding.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger log = LoggerFactory.getLogger(Constant.ROCKETMQ_REMOTING);

    private static final int FRAME_MAX_LENGTH =
        Integer.parseInt(System.getProperty("com.rocketmq.remoting.frameMaxLength", "16777216"));
    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 2, 4);
    }

    //  4字节长度 + 2字节扩展头部（2字节flag[最低位：请求类型，请求或响应] ） + 2字节属性长度 + N字节属性 +  custom body

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            // 2字节 flag，目前只用来最低位：表示请求类型：0表示请求；1：表示响应
            short flag = frame.readShort();
            short propertiesLen = frame.readShort();
            byte[] property = new byte[propertiesLen];
            frame.readBytes(property);
            RemotingCommand cmd = JSON.parseObject(new String(property), RemotingCommand.class);
            boolean request = ((flag & 0x01 ) == 0);  //最低位 0：表示请求，1：表示响应
            if(!request) {
                cmd.markResponseType();
            }
            // 剩下的内容为body，由具体的业务处理器进行解码
            if(frame.isReadable()) {
                ByteBuf bodyByteBuf = frame.slice().retain();
                cmd.setBodyByteBuf(bodyByteBuf);
            }
            return cmd;
        } catch (Exception e) {
            log.error("decode exception", e);
            RemotingUtil.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                //进行释放，由于使用了slince，并且调用了 retain，此次的释放，只是引用减少，在业务处理器完成解码后，需要继续释放
                frame.release();
            }
        }
        return null;
    }
}
