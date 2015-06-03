<?php

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

$newestSeries = $bs->getNewest();

echo 'Latest Series:<br />';

for($i=0; $i<5; $i++) {
	echo "{$newestSeries[$i]['series']} (ID: {$newestSeries[$i]['id']})<br />";
}
