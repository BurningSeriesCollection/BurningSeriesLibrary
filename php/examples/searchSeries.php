<?php

$search = 'Dragonball';
if(!empty($_GET['s'])) {
	$search = $_GET['s'];
}

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

// If the second parameter is set to "true" only exact matches will be returned
$series = $bs->search($search);

// Should be escaped normally but this is only an example
echo "Search for {$search} gave back: <br />";

foreach($series as $serie) {
	echo "{$serie['series']} (ID: {$serie['id']}) <br />";
}
