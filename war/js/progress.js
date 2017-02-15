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