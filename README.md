# websocket
netty-websocket

#说明
netty实现websocket，通过配置文件来启动,使用该websocket需要有redis消息队列进行支撑，支持ws
以及wss

#启动项目
1.拉取代码后，通过maven install生成编译文件

2.将lib包以及websocket-1.0.jar放入服务器任意目录,比如/usr/local/websocket

3.将配置文件websocket.properties放入/usr/local/websocket目录下

4.修改websocket.properties配置文件


#配置文件说明
DB_ENTRY=1：表示redis密码已经加密 0:表示redis密码没有加密，第一次运行请将DB_ENTRY=0

REDIS_HOST=redis服务器地址

REDIS_PORT=redis服务器端口号

REDIS_PASSWORD=redis密码

REDIS_DB=redis数据库

REDIS_MAX_ACTIVE=1024

REDIS_MAX_IDLE=200

REDIS_MAX_WAIT=10000

REDIS_TIMEOUT=10000

JKS_PATH=JKS证书所在的路径

JKS_PSD=证书密码

WEB_SOCKET_PORT=websocket端口号

WS_NAME=ws  表示websocket地址目录

WEB_SOCKET_EXECUTE_TIMES=200  redis每次执行多少条消息

WEB_SOCKET_EXECUTE_CRON=0/5 * * * * ? redis每隔多少秒执行消息

WEB_SOCKET_KEY=WEB_SCOKET redis的key

#客户端访问websocket地址
wss://服务器地址:端口号/ws

  
