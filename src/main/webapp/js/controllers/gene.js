angular.module("BZRFlag").controller("GeneCtrl", 
	["$scope", "Game", "$timeout", "$routeParams","Gene",
	function($scope, Game, $timeout, $routeParams, Gene){

	// Models
	var data = {};

	$scope.data = data;
	data.genes = Gene.query(function(){
		processGenes();
	});
	data.results = [];

	// Get fitness runs for each gene
	function processGenes() {

		for (var i = data.genes.length - 1; i >= 0; i--) {
			var gene = data.genes[i];
			(function(g) {
				g.fitness = g.$get(function(){
					measureFitness(g);
				});
			})(gene);
		};
	}

	// Measure a genes fitness
	function measureFitness(gene) {

		gene.overallFitness = 0;
		gene.naiveFitness = 0;
		var across = [],
			side = [];

		// Collect genes in pool based on direction
		for (var i = gene.fitness.length - 1; i >= 0; i--) {

			var geneRun = gene.fitness[i];
			var wasAcross = determineDirectionOfRun(geneRun);

			if (wasAcross)
				across.push(geneRun);
			else
				side.push(geneRun);

			gene.naiveFitness += geneRun.fitness;

		};

		gene.naiveFitness = (gene.fitness.length == 0) ? 0 : gene.naiveFitness/(gene.fitness.length);

		// Collect average of each bucket
		// Disclude bad values
		var acrossAvg, sideAvg, discountAcross = 0, discountSide = 0;
		var runningAcrTotal = 0, runningSideTotal = 0;
		for (var i = across.length - 1; i >= 0; i--) {
			var geneRun = across[i];
			if (geneRun.fitness > 10000)
				runningAcrTotal += geneRun.fitness;
			else
				discountAcross++
		};
		for (var i = side.length - 1; i >= 0; i--) {
			var geneRun = side[i];
			if (geneRun.fitness > 10000)
				runningSideTotal += geneRun.fitness;
			else
				discountSide++;
		};

		acrossAvg = (across.length == 0) ? 0 : runningAcrTotal/(across.length - discountAcross);
		sideAvg = (side.length == 0) ? 0 : runningSideTotal/(side.length - discountSide);

		sideAvg = (sideAvg == 0) ? acrossAvg : sideAvg;
		acrossAvg = (acrossAvg == 0) ? sideAvg : acrossAvg;

		gene.overallFitness = (acrossAvg + sideAvg) / 2;
		if (gene.overallFitness)
			data.results.push(gene);

	}

	// Use map, if gene crosses board, it gets bigger weight
	//
	//           blue
	//
	// red                  green
	//
	//          purple 
	function determineDirectionOfRun(geneRun) {

		var isAcross = false;

		// Determine if run timed out
		if (geneRun.note.indexOf("TIMEOUT") != -1) {
			geneRun.timeout = true;
			geneRun.note = geneRun.note.substring(0,geneRun.note.indexOf("TIMEOUT"));
		}

		var direction = geneRun.note.split("->");
		switch (direction[0]) {
			case 'red':
				if (direction[1] == 'green')
					isAcross = true;
				break;
			case 'blue':
				if (direction[1] == 'purple')
					isAcross = true;
				break;
			case 'green':
				if (direction[1] == 'red')
					isAcross = true;
				break;
			case 'purple':
				if (direction[1] == 'blue')
					isAcross = true;
				break;
		}

		return isAcross;
	}
	
}])