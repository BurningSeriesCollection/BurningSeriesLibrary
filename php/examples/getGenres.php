<?php

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

$genres = $bs->getGenres();

echo 'The following genres exist: <br />';

foreach($genres as $genre) {
	echo "{$genre} <br />";
}
