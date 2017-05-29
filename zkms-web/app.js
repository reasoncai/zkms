var express = require('express');
var zookeeper = require('node-zookeeper-client');
var httpProxy = require('http-proxy');
var ZOOKEEPER_ADDRESS = '127.0.0.1:2181';
var REGISTRY_ROOT = '/registry';
var port = 1234;
//连接zk
var zk = zookeeper.createClient(ZOOKEEPER_ADDRESS);
zk.connect();

//创建代理服务器对象并监听错误事件
var proxy = httpProxy.createProxyServer();
proxy.on('error',function(error,req,res){
	res.end();//输出空白响应数据
});

//启动web服务器
var app = express();
app.use(express.static('public'));
app.all("*",function(req,res){
	//处理图标请求
	if(req.path =='/favicon.ico'){
		res.end();
		return;
	}
	//获取服务名称
	var serviceName = req.get('Service-Name');
	console.log('serviceName : '+serviceName);
	if(!serviceName){
		console.log('Service-Name request head is not exist');
		res.end();
		return;
	}
	//获取服务路径
	var servicePath = REGISTRY_ROOT + '/' + serviceName;
	console.log('servicePath : '+ servicePath);
	//获取服务路径下的地址节点
	zk.getChildren(servicePath,function(error,addressNodes){
		if(error){
			console.log(error.stack);
			res.end();
			return;
		}
		var size = addressNodes.length;
		if(size==0){
			console.log('address node is not exist');
			res.end()
			return;
		}
		//生成路径地址
		var addressPath = servicePath + '/';
		if(size == 1){
			//只有一个地址
			addressPath += addressNodes[0];
		}else{
			//存在多个地址，随机获取一个
			addressPath += addressNodes[parseInt(Math.random()*size)];
		}
		console.log('addressPath : '+ addressPath);

		//获取服务地址
		zk.getData(addressPath,function(error,serviceAddress){
			if(error){
			console.log(error.stack);
			res.end();
			return;
			}
			if(!serviceAddress){
				console.log('serviceAddress is not exist');
				res.end();
				return;
			}
			console.log('serviceAddress : '+serviceAddress);

			//执行反向代理
			proxy.web(req,res,{
				target : 'http://'+serviceAddress //目标地址
			});
		});
	});
});

app.listen(port,function(){
	console.log('server is running at %d',port);
});