var network;
var options;
var nodes;
var edges;

function initNetwork(container) {
	nodes = new vis.DataSet([ {
		id : 0,
		label : 'pkg.0',
		group: 0
	}, {
		id : 1,
		label : 'pkg.1',
		group: 1
	}, {
		id : 2,
		label : 'pkg.2',
		group: 2
	}, {
		id : 3,
		label : 'pkg.3',
		group: 3
	}, {
		id : 4,
		label : 'class.1',
		group: 1
	}, {
		id : 5,
		label : 'class.2',
		group: 2
	}, {
		id : 6,
		label : 'class.3',
		group: 3
	}, ]);

	edges = new vis.DataSet([ {
		from : 0,
		to : 1
	}, {
		from : 0,
		to : 2
	}, {
		from : 0,
		to : 3
	}, {
		from : 1,
		to : 4
	}, {
		from : 2,
		to : 5
	}, {
		from : 3,
		to : 6
	} ]);

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
			shape : 'dot',
			size : 30,
			font : {
				size : 32,
				color : '#ffffff'
			},
			borderWidth : 2
		},
		edges : {
			width : 2
		},
		physics : {
			enabled: true,
			stabilization: {
				iterations: 1
			}
		}
	};

	network = new vis.Network(container, data, options);
	network.on("doubleClick", function(params) {
		if (params.nodes.length == 1) {
			if (network.isCluster(params.nodes[0]) == true) {
				network.openCluster(params.nodes[0]);
			} else {
				network.clusterByConnection(params.nodes[0]);
			}
		}
	});
}
