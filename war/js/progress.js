var mprogress = new Mprogress({
	template : 3,
	parent : '#vizSourceStructureNetwork'
});

function startIndeterminateProgress() {
	mprogress.start();
}

function stopIndeterminateProgress() {
	mprogress.end();
}