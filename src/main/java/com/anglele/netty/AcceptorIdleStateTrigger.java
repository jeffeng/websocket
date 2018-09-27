package com.anglele.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {
    private int unReceivedCounts=0;
   
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    		throws Exception {
    	//有消息时归零
    	unReceivedCounts=0;
    	super.channelRead(ctx, msg);
    }
    
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			//在这里并处理业务逻辑
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.READER_IDLE) {
				unReceivedCounts++;
				System.out.println(unReceivedCounts);
				if (unReceivedCounts>10) {
					ctx.close();
					unReceivedCounts = 0;
				}				
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}		
	}

}
