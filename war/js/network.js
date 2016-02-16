var network;
var options;
var nodes;
var edges;

function initNetwork(container) {
	var nodes = [ {
		id : 1,
		label : 'Node 1',
		color : 'orange'
	}, {
		id : 2,
		label : 'Node 2',
		color : 'DarkViolet',
		font : {
			color : 'white'
		}
	}, {
		id : 3,
		label : 'Node 3',
		color : 'orange'
	}, {
		id : 4,
		label : 'Node 4',
		color : 'DarkViolet',
		font : {
			color : 'white'
		}
	}, {
		id : 5,
		label : 'Node 5',
		color : 'orange'
	}, {
		id : 6,
		label : 'cid = 1',
		cid : 1,
		color : 'orange'
	}, {
		id : 7,
		label : 'cid = 1',
		cid : 1,
		color : 'DarkViolet',
		font : {
			color : 'white'
		}
	}, {
		id : 8,
		label : 'cid = 1',
		cid : 1,
		color : 'lime'
	}, {
		id : 9,
		label : 'cid = 1',
		cid : 1,
		color : 'orange'
	}, {
		id : 10,
		label : 'cid = 1',
		cid : 1,
		color : 'lime'
	} ];

	// create an array with edges
	var edges = [ {
		from : 1,
		to : 2
	}, {
		from : 1,
		to : 3
	}, {
		from : 10,
		to : 4
	}, {
		from : 2,
		to : 5
	}, {
		from : 6,
		to : 2
	}, {
		from : 7,
		to : 5
	}, {
		from : 8,
		to : 6
	}, {
		from : 9,
		to : 7
	}, {
		from : 10,
		to : 9
	} ];

	var data = {
		nodes : nodes,
		edges : edges
	};
	var options = {
		autoResize : true,
		clickToUse: true,
		height : '100%',
		width : '100%'
	};

	network = new vis.Network(container, data, options);
	network.on("selectNode", function(params) {
		if (params.nodes.length == 1) {
			if (network.isCluster(params.nodes[0]) == true) {
				network.openCluster(params.nodes[0]);
			}
		}
	});
}
