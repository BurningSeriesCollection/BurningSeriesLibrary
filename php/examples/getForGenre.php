<?php

$genre = 'Anime';
if(!empty($_GET['g'])) {
	$genre = $_GET['g'];
}

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

$series = $bs->getByGenre($genre);

echo "The following series have genre {$genre}: <br />";

foreach($series['series'] as $serie) {
	echo "{$serie['name']} (ID: {$serie['id']}) <br />";
}
