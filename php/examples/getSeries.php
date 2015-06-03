<?php

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

$series = $bs->getSeries();
$numSeries = count($series);

echo "We currently have {$numSeries} Series: <br />";

foreach($series as $serie) {
	echo "{$serie['series']} (ID: {$serie['id']}) <br />";
}
