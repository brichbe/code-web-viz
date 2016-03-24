var network;
var options;
var nodes;
var edges;

function initNetwork(container) {
	nodes = new vis.DataSet();
	edges = new vis.DataSet();

	var data = {
		nodes : nodes,
		edges : edges
	};
	var options = {
		autoResize : true,
		clickToUse : false,
		height : '100%',
		width : '100%',
		nodes : {
			shape : 'box',
			size : 30,
			font : {
				size : 32
			},
			borderWidth : 2
		},
		edges : {
			smooth : false,
			width : 2
		},
		layout : {
			hierarchical : {
				direction : 'UD',
				nodeSpacing : 200,
				sortMethod : 'directed'
			}
		},
		physics : {
			enabled : false
		}
	};

	network = new vis.Network(container, data, options);
	network.on("doubleClick", function(params) {
		if (params.nodes.length == 1) {
			if (network.isCluster(params.nodes[0]) == true) {
				network.openCluster(params.nodes[0]);
			} else {
				var nodeId = params.nodes[0];
				var node = nodes.get(nodeId);
				var nodesToCluser = [];
				nodesToCluser.push(nodeId);
				var edgesArray = edges.get();
				for (var index = 0; index < edgesArray.length; index++) {
					var edge = edgesArray[index];
					if (nodeId === edge.from) {
						nodesToCluser.push(edge.to);
					}
				}
				var clusterOptions = {
					joinCondition : function(childOptions) {
						return nodesToCluser.indexOf(childOptions.id) != -1;
					},
					clusterNodeProperties : {
						borderWidth : 3,
						shape : 'image',
						image : 'images/clustering.png',
						font : '24px arial #ffffff',
						color : node.color,
						label : node.label + ' (Cluster)',
						title : 'Cluster of ' + node.label
					}
				};
				network.cluster(clusterOptions);
			}
		}
	});
}

function setNetworkData(nodesJson, edgesJson) {
	nodes.clear();
	nodes.add(nodesJson);
	edges.clear();
	edges.add(edgesJson);
	var data = {
		nodes : nodes,
		edges : edges
	};
	network.setData(data);
}