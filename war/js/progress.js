var networkIndeterminateProgress = new Mprogress({
	template : 3,
	parent : '#vizSourceStructureNetwork'
});

function startNetworkIndeterminateProgress() {
	networkIndeterminateProgress.start();
}

function stopNetworkIndeterminateProgress() {
	networkIndeterminateProgress.end();
}

var networkDeterminateProgress = new Mprogress({
	template : 1,
	parent : '#vizSourceStructureNetwork'
});

function startNetworkDeterminateProgress() {
	networkDeterminateProgress.start();
}

function setNetworkDeterminateProgress(percentage) {
	networkDeterminateProgress.set(percentage);
}

function stopNetworkDeterminateProgress() {
	networkDeterminateProgress.end();
}